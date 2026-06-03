package com.base.ocr.infrastructure.provider;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.base.ocr.domain.model.BankCardResult;
import com.base.ocr.domain.model.IdCardResult;
import com.base.ocr.domain.model.InvoiceResult;
import com.base.ocr.domain.service.OcrProvider;
import com.base.ocr.infrastructure.config.OcrSourceConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

/**
 * 腾讯云 OCR 识别提供者
 * <p>
 * 使用 TC3-HMAC-SHA256 签名直接调用 API，不引入 SDK。
 * </p>
 *
 * @author base
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TencentOcrProvider implements OcrProvider {

    private static final String SERVICE = "ocr";
    private static final String HOST = "ocr.tencentcloudapi.com";
    private static final String ENDPOINT = "https://" + HOST;
    private static final String ALGORITHM = "TC3-HMAC-SHA256";
    private static final String VERSION = "2018-11-19";
    private static final int TIMEOUT = 30000;

    private final OcrSourceConfig config;

    @Override
    public String getName() {
        return "tencent";
    }

    @Override
    public String getDisplayName() {
        return "腾讯云";
    }

    @Override
    public IdCardResult recognizeIdCard(byte[] imageData, String side) {
        String imageBase64 = Base64.getEncoder().encodeToString(imageData);
        String cardSide = "front".equalsIgnoreCase(side) ? "FRONT" : "BACK";

        JSONObject body = new JSONObject();
        body.put("ImageBase64", imageBase64);
        body.put("CardSide", cardSide);

        JSONObject response = callApi("IDCardOCR", body.toJSONString());
        if (response == null) {
            return null;
        }

        IdCardResult result = new IdCardResult();
        result.setSide(side);
        result.setProvider(getName());

        if ("FRONT".equals(cardSide)) {
            result.setName(response.getString("Name"));
            result.setGender(response.getString("Sex"));
            result.setNation(response.getString("Nation"));
            result.setBirthday(response.getString("Birth"));
            result.setAddress(response.getString("Address"));
            result.setIdNumber(response.getString("IdNum"));
        } else {
            result.setAuthority(response.getString("Authority"));
            result.setValidDateStart(parseValidDate(response.getString("ValidDate"), true));
            result.setValidDateEnd(parseValidDate(response.getString("ValidDate"), false));
        }

        return result;
    }

    @Override
    public InvoiceResult recognizeInvoice(byte[] fileData, boolean isPdf) {
        String fileBase64 = Base64.getEncoder().encodeToString(fileData);

        JSONObject body = new JSONObject();
        if (isPdf) {
            body.put("FileBase64", fileBase64);
            body.put("IsPdf", true);
            body.put("PdfPageNumber", 1);
        } else {
            body.put("ImageBase64", fileBase64);
        }

        JSONObject response = callApi("VatInvoiceOCR", body.toJSONString());
        if (response == null) {
            return null;
        }

        InvoiceResult result = new InvoiceResult();
        result.setProvider(getName());

        com.alibaba.fastjson2.JSONArray infos = response.getJSONArray("VatInvoiceInfos");
        if (infos != null) {
            for (int i = 0; i < infos.size(); i++) {
                JSONObject item = infos.getJSONObject(i);
                mapInvoiceField(result, item.getString("Name"), item.getString("Value"));
            }
        }

        return result;
    }

    @Override
    public BankCardResult recognizeBankCard(byte[] imageData) {
        String imageBase64 = Base64.getEncoder().encodeToString(imageData);

        JSONObject body = new JSONObject();
        body.put("ImageBase64", imageBase64);

        JSONObject response = callApi("BankCardOCR", body.toJSONString());
        if (response == null) {
            return null;
        }

        BankCardResult result = new BankCardResult();
        result.setProvider(getName());
        result.setCardNumber(response.getString("CardNo"));
        result.setBankName(response.getString("BankInfo"));
        result.setCardType(parseBankCardType(response.getString("CardType")));
        result.setValidDate(response.getString("ValidDate"));

        return result;
    }

    /**
     * 调用腾讯云 OCR API
     */
    private JSONObject callApi(String action, String payload) {
        String secretId = config.getTencentSecretId();
        String secretKey = config.getTencentSecretKey();
        if (secretId.isEmpty() || secretKey.isEmpty()) {
            log.warn("腾讯云 OCR SecretId/SecretKey 未配置");
            return null;
        }

        try {
            long timestamp = Instant.now().getEpochSecond();
            String date = Instant.ofEpochSecond(timestamp)
                    .atZone(ZoneOffset.UTC)
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

            String authorization = buildAuthorization(secretId, secretKey, payload, timestamp, date);

            HttpResponse httpResponse = HttpRequest.post(ENDPOINT)
                    .header("Content-Type", "application/json; charset=utf-8")
                    .header("Host", HOST)
                    .header("X-TC-Action", action)
                    .header("X-TC-Version", VERSION)
                    .header("X-TC-Timestamp", String.valueOf(timestamp))
                    .header("Authorization", authorization)
                    .body(payload)
                    .timeout(TIMEOUT)
                    .execute();

            JSONObject json = JSON.parseObject(httpResponse.body());
            JSONObject resp = json.getJSONObject("Response");

            if (resp == null) {
                log.error("腾讯云 OCR 响应异常: action={}, body={}", action, httpResponse.body());
                return null;
            }

            JSONObject error = resp.getJSONObject("Error");
            if (error != null) {
                log.error("腾讯云 OCR 调用失败: action={}, code={}, message={}",
                        action, error.getString("Code"), error.getString("Message"));
                return null;
            }

            return resp;
        } catch (Exception e) {
            log.error("腾讯云 OCR 请求异常: action={}, error={}", action, e.getMessage(), e);
            return null;
        }
    }

    private String buildAuthorization(String secretId, String secretKey, String payload,
                                      long timestamp, String date) throws Exception {
        String canonicalHeaders = "content-type:application/json; charset=utf-8\nhost:" + HOST + "\n";
        String signedHeaders = "content-type;host";
        String hashedPayload = sha256Hex(payload);
        String canonicalRequest = "POST\n/\n\n" + canonicalHeaders + "\n" + signedHeaders + "\n" + hashedPayload;

        String credentialScope = date + "/" + SERVICE + "/tc3_request";
        String stringToSign = ALGORITHM + "\n" + timestamp + "\n" + credentialScope + "\n" + sha256Hex(canonicalRequest);

        byte[] secretDate = hmacSha256(("TC3" + secretKey).getBytes(StandardCharsets.UTF_8), date);
        byte[] secretService = hmacSha256(secretDate, SERVICE);
        byte[] secretSigning = hmacSha256(secretService, "tc3_request");
        String signature = bytesToHex(hmacSha256(secretSigning, stringToSign));

        return ALGORITHM + " Credential=" + secretId + "/" + credentialScope
                + ", SignedHeaders=" + signedHeaders + ", Signature=" + signature;
    }

    private static byte[] hmacSha256(byte[] key, String msg) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(key, "HmacSHA256"));
        return mac.doFinal(msg.getBytes(StandardCharsets.UTF_8));
    }

    private static String sha256Hex(String data) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hash = md.digest(data.getBytes(StandardCharsets.UTF_8));
        return bytesToHex(hash);
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    private String parseValidDate(String validDate, boolean isStart) {
        if (validDate == null || !validDate.contains("-")) {
            return validDate;
        }
        String[] parts = validDate.split("-");
        return isStart ? parts[0].trim() : parts[1].trim();
    }

    private void mapInvoiceField(InvoiceResult result, String name, String value) {
        if (name == null || value == null) {
            return;
        }
        switch (name) {
            case "发票类型":
                result.setInvoiceType(value);
                break;
            case "发票代码":
                result.setInvoiceCode(value);
                break;
            case "发票号码":
                result.setInvoiceNumber(value);
                break;
            case "开票日期":
                result.setInvoiceDate(value);
                break;
            case "校验码":
                result.setCheckCode(value);
                break;
            case "购买方名称":
                result.setBuyerName(value);
                break;
            case "购买方识别号":
                result.setBuyerTaxId(value);
                break;
            case "销售方名称":
                result.setSellerName(value);
                break;
            case "销售方识别号":
                result.setSellerTaxId(value);
                break;
            case "合计金额":
                result.setTotalAmount(value);
                break;
            case "合计税额":
                result.setTotalTax(value);
                break;
            case "价税合计(大写)":
                result.setAmountInWords(value);
                break;
            case "价税合计(小写)":
                result.setAmountInFigures(value);
                break;
            default:
                break;
        }
    }

    private String parseBankCardType(String cardType) {
        if (cardType == null) {
            return null;
        }
        switch (cardType) {
            case "0":
                return "借记卡";
            case "1":
                return "信用卡";
            default:
                return cardType;
        }
    }
}
