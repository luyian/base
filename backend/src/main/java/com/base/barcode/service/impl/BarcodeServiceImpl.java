package com.base.barcode.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.base.ai.config.AiSkillConfig;
import com.base.barcode.dto.BarcodeGenerateResult;
import com.base.barcode.dto.BarcodeListResponse;
import com.base.barcode.dto.BarcodeRecordRequest;
import com.base.barcode.entity.Barcode;
import com.base.barcode.mapper.BarcodeMapper;
import com.base.barcode.service.BarcodeService;
import com.base.barcode.util.BarcodeGeneratorUtil;
import com.base.common.exception.BusinessException;
import com.base.common.service.CosService;
import com.base.system.entity.SysFile;
import com.base.system.service.FileService;
import com.base.system.util.SecurityUtils;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 通用条码/二维码服务实现类
 *
 * @author base
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BarcodeServiceImpl implements BarcodeService {

    /** 允许的图片扩展名 */
    private static final String[] IMAGE_EXTENSIONS = {".jpg", ".jpeg", ".png", ".bmp", ".webp"};

    /** 条码类型：CODE128 条形码 */
    private static final int TYPE_CODE128 = 1;

    /** 条码类型：QR 二维码 */
    private static final int TYPE_QR = 2;

    /** 来源：自生成 */
    private static final int SOURCE_GENERATED = 1;

    /** COS/系统文件分组：条码 */
    private static final String FILE_GROUP = "barcode";

    /** 二维码编号前缀 */
    private static final String PREFIX_QR = "BR";

    /** 条形码编号前缀 */
    private static final String PREFIX_BAR = "BAR";

    /** 单次批量生成上限 */
    private static final int MAX_BATCH = 200;

    /** 生成唯一编号最大重试次数（uk_code 兜底容错） */
    private static final int MAX_RETRY = 5;

    /** 编号日期格式 */
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");

    private final BarcodeMapper barcodeMapper;
    private final AiSkillConfig aiSkillConfig;
    private final FileService fileService;
    private final CosService cosService;

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<BarcodeGenerateResult> generateBatch(String type, int count) {
        if (count < 1 || count > MAX_BATCH) {
            throw new BusinessException(400, "生成数量须在 1~" + MAX_BATCH + " 之间");
        }
        boolean qr = "QR".equalsIgnoreCase(type);
        if (!qr && !"CODE128".equalsIgnoreCase(type)) {
            throw new BusinessException(400, "条码类型仅支持 CODE128 或 QR");
        }
        Long userId = SecurityUtils.getCurrentUserId();
        String prefix = (qr ? PREFIX_QR : PREFIX_BAR) + LocalDate.now().format(DATE_FORMAT);

        List<BarcodeGenerateResult> result = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            String code = nextUniqueCode(prefix);
            byte[] png;
            try {
                png = qr ? generateQr(code) : generateCode128(code);
            } catch (Exception e) {
                log.error("批量生成条码图片失败，code: {}", code, e);
                throw new BusinessException("条码生成失败，请重试");
            }
            SysFile sysFile = fileService.uploadBytes(png, "barcode.png", FILE_GROUP, "条码/二维码生成", "image/png");
            Barcode barcode = new Barcode();
            barcode.setCode(code);
            barcode.setType(qr ? TYPE_QR : TYPE_CODE128);
            barcode.setSource(SOURCE_GENERATED);
            barcode.setUserId(userId);
            barcode.setFileId(sysFile.getId());
            barcodeMapper.insert(barcode);
            result.add(BarcodeGenerateResult.builder()
                    .code(barcode.getCode())
                    .id(barcode.getId())
                    .fileId(sysFile.getId())
                    .filePath(sysFile.getFilePath())
                    .fileUrl(cosService.getFileUrl(sysFile.getFilePath()))
                    .bound(false)
                    .exists(false)
                    .build());
        }
        log.info("批量生成条码成功，userId: {}, type: {}, count: {}", userId, type, count);
        return result;
    }

    @Override
    public Page<BarcodeListResponse> listBarcodes(Long userId, Integer type, Boolean bound, long page, long size) {
        LambdaQueryWrapper<Barcode> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Barcode::getUserId, userId)
                .eq(type != null, Barcode::getType, type);
        if (bound != null) {
            if (bound) {
                wrapper.isNotNull(Barcode::getBizId);
            } else {
                wrapper.isNull(Barcode::getBizId);
            }
        }
        wrapper.orderByDesc(Barcode::getId);
        Page<Barcode> result = barcodeMapper.selectPage(new Page<>(page, size), wrapper);
        List<BarcodeListResponse> records = result.getRecords().stream()
                .map(this::toListResponse)
                .collect(Collectors.toList());
        Page<BarcodeListResponse> responsePage = new Page<>(result.getCurrent(), result.getSize(), result.getTotal());
        responsePage.setRecords(records);
        return responsePage;
    }

    /**
     * 按前缀生成当天递增的全局唯一编号（QR/CODE + yyyyMMdd + 4 位递增，uk_code 兜底）
     */
    private String nextUniqueCode(String prefix) {
        for (int i = 0; i < MAX_RETRY; i++) {
            // 用原生 SQL 含软删行一起统计/校验，避免软删行占用的 uk_code 被重新分配而 Duplicate entry
            long seq = barcodeMapper.countAllByCodePrefix(prefix) + 1;
            String code = prefix + String.format("%04d", seq);
            if (barcodeMapper.countAllByCode(code) == 0) {
                return code;
            }
        }
        throw new BusinessException("生成条码编号失败，请重试");
    }

    /**
     * 条码记录转列表项（解析归档图片预签名 URL 与绑定态）
     */
    private BarcodeListResponse toListResponse(Barcode barcode) {
        String filePath = null;
        if (barcode.getFileId() != null) {
            SysFile sysFile = fileService.getFileById(barcode.getFileId());
            if (sysFile != null) {
                filePath = sysFile.getFilePath();
            }
        }
        BarcodeListResponse response = new BarcodeListResponse();
        response.setId(barcode.getId());
        response.setCode(barcode.getCode());
        response.setType(barcode.getType());
        response.setBound(barcode.getBizId() != null);
        response.setFileUrl(filePath != null ? cosService.getFileUrl(filePath) : null);
        response.setCreateTime(barcode.getCreateTime());
        return response;
    }

    @Override
    public BarcodeGenerateResult generate(String content, String type) {
        if (!StringUtils.hasText(content)) {
            throw new BusinessException(400, "条码内容不能为空");
        }
        byte[] png;
        try {
            if ("QR".equalsIgnoreCase(type)) {
                png = generateQr(content);
            } else if ("CODE128".equalsIgnoreCase(type)) {
                png = generateCode128(content);
            } else {
                throw new BusinessException(400, "条码类型仅支持 CODE128 或 QR");
            }
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("生成条码失败，type: {}, content: {}", type, content, e);
            throw new BusinessException("条码生成失败，请检查内容是否支持该码制");
        }

        Long userId = SecurityUtils.getCurrentUserId();
        // 生成即落库：同内容全局唯一，命中已有记录则复用（不重传图、不新建）
        Barcode existed = getExistingByCode(content);
        if (existed != null) {
            return buildResult(existed, true);
        }

        // 首次生成：传图到 COS + 记 sys_file，再落 t_barcode（未绑定态）
        SysFile sysFile = fileService.uploadBytes(png, "barcode.png", FILE_GROUP, "条码/二维码生成", "image/png");
        Barcode barcode = new Barcode();
        barcode.setCode(content);
        barcode.setType("QR".equalsIgnoreCase(type) ? TYPE_QR : TYPE_CODE128);
        barcode.setSource(SOURCE_GENERATED);
        barcode.setUserId(userId);
        barcode.setFileId(sysFile.getId());
        try {
            barcodeMapper.insert(barcode);
        } catch (DuplicateKeyException e) {
            // 并发同内容：uk_code 索引兜底，回退复用已有记录
            log.warn("并发生成同内容条码，code: {}", content);
            Barcode exist = getExistingByCode(content);
            if (exist != null) {
                return buildResult(exist, true);
            }
            throw new BusinessException("条码登记失败，请重试");
        }
        log.info("生成条码并落库成功，userId: {}, code: {}, id: {}", userId, content, barcode.getId());
        return buildResult(barcode, false);
    }

    /**
     * 按内容查询未删除的条码记录（全局唯一）
     */
    private Barcode getExistingByCode(String code) {
        LambdaQueryWrapper<Barcode> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Barcode::getCode, code).eq(Barcode::getDeleted, 0).last("LIMIT 1");
        return barcodeMapper.selectOne(wrapper);
    }

    /**
     * 组装生成结果：据 file_id 取 COS Key 生成预签名 URL，bound 反映是否已绑定
     */
    private BarcodeGenerateResult buildResult(Barcode barcode, boolean exists) {
        String filePath = null;
        Long fileId = barcode.getFileId();
        if (fileId != null) {
            SysFile sysFile = fileService.getFileById(fileId);
            if (sysFile != null) {
                filePath = sysFile.getFilePath();
            }
        }
        String fileUrl = filePath != null ? cosService.getFileUrl(filePath) : null;
        return BarcodeGenerateResult.builder()
                .code(barcode.getCode())
                .id(barcode.getId())
                .fileId(fileId)
                .filePath(filePath)
                .fileUrl(fileUrl)
                .bound(barcode.getBizId() != null)
                .exists(exists)
                .build();
    }

    /**
     * 生成二维码 PNG
     */
    private byte[] generateQr(String content) throws Exception {
        Map<com.google.zxing.EncodeHintType, Object> hints = new HashMap<>(4);
        hints.put(com.google.zxing.EncodeHintType.CHARACTER_SET, "UTF-8");
        hints.put(com.google.zxing.EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);
        hints.put(com.google.zxing.EncodeHintType.MARGIN, 1);
        BitMatrix matrix = new com.google.zxing.qrcode.QRCodeWriter()
                .encode(content, BarcodeFormat.QR_CODE, 300, 300, hints);
        return BarcodeGeneratorUtil.renderPng(matrix);
    }

    /**
     * 生成 CODE128 条形码 PNG
     */
    private byte[] generateCode128(String content) throws Exception {
        Map<com.google.zxing.EncodeHintType, Object> hints = new HashMap<>(2);
        hints.put(com.google.zxing.EncodeHintType.CHARACTER_SET, "UTF-8");
        hints.put(com.google.zxing.EncodeHintType.MARGIN, 2);
        BitMatrix matrix = new com.google.zxing.oned.Code128Writer()
                .encode(content, BarcodeFormat.CODE_128, 400, 120, hints);
        return BarcodeGeneratorUtil.renderPng(matrix);
    }

    @Override
    public Long record(Long userId, BarcodeRecordRequest request) {
        if (!StringUtils.hasText(request.getCode())) {
            throw new BusinessException(400, "条码内容不能为空");
        }
        if (getExistingByCode(request.getCode()) != null) {
            throw new BusinessException(400, "该条码内容已存在，请勿重复登记");
        }

        Barcode barcode = new Barcode();
        barcode.setCode(request.getCode());
        barcode.setType(request.getType() != null ? request.getType() : 1);
        barcode.setSource(request.getSource() != null ? request.getSource() : 1);
        if (StringUtils.hasText(request.getBizType())) {
            barcode.setBizType(request.getBizType());
            barcode.setBizId(request.getBizId());
        }
        barcode.setUserId(userId);
        barcode.setRemark(request.getRemark());
        barcodeMapper.insert(barcode);
        log.info("登记通用条码记录成功，userId: {}, code: {}, bizType: {}, bizId: {}",
                userId, request.getCode(), request.getBizType(), request.getBizId());
        return barcode.getId();
    }

    @Override
    public Barcode getByCode(String code) {
        return getExistingByCode(code);
    }

    @Override
    public void deleteRecord(Long userId, Long id) {
        Barcode barcode = barcodeMapper.selectById(id);
        if (barcode == null || (barcode.getDeleted() != null && barcode.getDeleted() == 1)) {
            throw new BusinessException(404, "条码记录不存在");
        }
        if (!barcode.getUserId().equals(userId)) {
            throw new BusinessException(403, "无权操作该条码记录");
        }
        barcodeMapper.deleteById(id);
        log.info("删除条码记录成功，userId: {}, id: {}", userId, id);
    }

    @Override
    public Map<String, Object> decodeImage(MultipartFile file) {
        String originalName = file.getOriginalFilename() == null ? "unknown.png" : file.getOriginalFilename();
        String lower = originalName.toLowerCase();
        boolean validExt = false;
        for (String ext : IMAGE_EXTENSIONS) {
            if (lower.endsWith(ext)) {
                validExt = true;
                break;
            }
        }
        if (!validExt) {
            throw new BusinessException(400, "不支持的图片格式，仅支持 jpg/jpeg/png/bmp/webp");
        }

        String url = aiSkillConfig.getPythonToolsUrl() + "/api/barcode/decode-image";
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            ByteArrayResource resource = new ByteArrayResource(file.getBytes()) {
                @Override
                public String getFilename() {
                    return originalName;
                }
            };
            HttpHeaders fileHeaders = new HttpHeaders();
            fileHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            fileHeaders.setContentDispositionFormData("file", originalName);
            body.add("file", new HttpEntity<>(resource, fileHeaders));

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
            ResponseEntity<String> response = restTemplate.exchange(
                    url, HttpMethod.POST, requestEntity, String.class);

            if (response.getStatusCode() != HttpStatus.OK || response.getBody() == null) {
                throw new BusinessException("识别服务异常，请稍后重试");
            }
            JSONObject result = JSON.parseObject(response.getBody());
            if (result == null) {
                throw new BusinessException("识别服务响应异常");
            }
            if (result.getIntValue("code") != 200) {
                throw new BusinessException(result.getString("message") != null
                        ? result.getString("message") : "识别失败");
            }
            String data = result.getString("data");
            log.info("图片条码识别成功，file: {}", originalName);
            Map<String, Object> out = new HashMap<>(1);
            out.put("content", parseContent(data));
            return out;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("调用 python-tools 识别条码失败，file: {}", originalName, e);
            throw new BusinessException("识别失败，请稍后重试");
        }
    }

    /**
     * 解析识别结果 json 中的 content
     */
    private String parseContent(String data) {
        if (data == null) {
            return null;
        }
        try {
            JSONObject obj = JSON.parseObject(data);
            return obj != null ? obj.getString("content") : data;
        } catch (Exception e) {
            return data;
        }
    }
}