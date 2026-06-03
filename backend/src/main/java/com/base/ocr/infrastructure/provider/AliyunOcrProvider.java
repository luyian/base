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
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.UUID;

/**
 * 阿里云 OCR 识别提供者
 * <p>
 * 使用阿里云 V3 签名直接调用 OCR API，不引入 SDK。
 * </p>
 *
 * @author base
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AliyunOcrProvider implements OcrProvider {

    private static final String HOST = "ocr-api.cn-hangzhou.aliyuncs.com";
    private static final String ENDPOINT = "https://" + HOST;
    private static final String API_VERSION = "2021-07-07";
    private static final String ALGORITHM = "ACS3-HMAC-SHA256";
    private static final int TIMEOUT = 30000;

    private final OcrSourceConfig config;

    @Override
    public String getName() {
        return "aliyun";
    }

    @Override
    public String getDisplayName() {
        return "阿里云";
    }

    @Override
    public IdCardResult recognizeIdCard(byte[] imageData, String side) {
        String imageBase64 = Base64.getEncoder().encodeToString(imageData);

        JSONObject body = new JSONObject();
        body.put("body", imageBase64);

        JSONObject response = callApi("RecognizeIdcard", body.toJSONString());
        if (response == null) {
            return null;
        }

        String dataStr = response.getString("Data");
        if (dataStr == null) {
            return null;
        }
        JSONObject data = JSON.parseObject(dataStr);
        if (data == null) {
            return null;
        }

        IdCardResult result = new IdCardResult();
        result.setSide(side);
        result.setProvider(getName());

        JSONObject face = data.getJSONObject("face");
        JSONObject back = data.getJSONObject("back");

        if ("front".equalsIgnoreCase(side) && face != null) {
            JSONObject faceData = face.getJSONObject("data");
            if (faceData != null) {
                result.setName(faceData.getString("name"));
                result.setGender(faceData.getString("sex"));
                result.setNation(faceData.getString("nationality"));
                result.setBirthday(faceData.getString("birthDate"));
                result.setAddress(faceData.getString("address"));
                result.setIdNumber(faceData.getString("idNumber"));
            }
        } else if ("back".equalsIgnoreCase(side) && back != null) {
            JSONObject backData = back.getJSONObject("data");
            if (backData != null) {
                result.setAuthority(backData.getString("issueAuthority"));
                result.setValidDateStart(backData.getString("startDate"));
                result.setValidDateEnd(backData.getString("endDate"));
            }
        }

        return result;
    }

    @Override
    public InvoiceResult recognizeInvoice(byte[] fileData, boolean isPdf) {
        String fileBase64 = Base64.getEncoder().encodeToString(fileData);

        JSONObject body = new JSONObject();
        body.put("body", fileBase64);

        JSONObject response = callApi("RecognizeInvoice", body.toJSONString());
        if (response == null) {
            return null;
        }

        String dataStr = response.getString("Data");
        if (dataStr == null) {
            return null;
        }
        JSONObject data = JSON.parseObject(dataStr);
        if (data == null) {
            return null;
        }

        InvoiceResult result = new InvoiceResult();
        result.setProvider(getName());
        result.setInvoiceType(data.getString("invoiceType"));
        result.setInvoiceCode(data.getString("invoiceCode"));
        result.setInvoiceNumber(data.getString("invoiceNo"));
        result.setInvoiceDate(data.getString("invoiceDate"));
        result.setCheckCode(data.getString("checkCode"));
        result.setBuyerName(data.getString("purchaserName"));
        result.setBuyerTaxId(data.getString("purchaserTaxNo"));
        result.setSellerName(data.getString("sellerName"));
        result.setSellerTaxId(data.getString("sellerTaxNo"));
        result.setTotalAmount(data.getString("invoiceAmountPreTax"));
        result.setTotalTax(data.getString("invoiceTax"));
        result.setAmountInWords(data.getString("invoiceAmountCn"));
        result.setAmountInFigures(data.getString("invoiceAmount"));

        return result;
    }

    @Override
    public BankCardResult recognizeBankCard(byte[] imageData) {
        String imageBase64 = Base64.getEncoder().encodeToString(imageData);

        JSONObject body = new JSONObject();
        body.put("body", imageBase64);

        JSONObject response = callApi("RecognizeBankCard", body.toJSONString());
        if (response == null) {
            return null;
        }

        String dataStr = response.getString("Data");
        if (dataStr == null) {
            return null;
        }
        JSONObject data = JSON.parseObject(dataStr);
        if (data == null) {
            return null;
        }

        BankCardResult result = new BankCardResult();
        result.setProvider(getName());
        result.setCardNumber(data.getString("cardNumber"));
        result.setBankName(data.getString("bankName"));
        result.setCardType(data.getString("cardType"));
        result.setValidDate(data.getString("validDate"));

        return result;
    }

    /**
     * 调用阿里云 OCR API（V3 签名）
     */
    private JSONObject callApi(String action, String payload) {
        String accessKeyId = config.getAliyunAccessKeyId();
        String accessKeySecret = config.getAliyunAccessKeySecret();
        if (accessKeyId.isEmpty() || accessKeySecret.isEmpty()) {
            log.warn("阿里云 OCR AccessKeyId/AccessKeySecret 未配置");
            return null;
        }

        try {
            String nonce = UUID.randomUUID().toString();
            String dateTime = ZonedDateTime.now(ZoneOffset.UTC)
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'"));

            String hashedPayload = sha256Hex(payload);
            String canonicalHeaders = "host:" + HOST + "\n"
                    + "x-acs-action:" + action + "\n"
                    + "x-acs-content-sha256:" + hashedPayload + "\n"
                    + "x-acs-date:" + dateTime + "\n"
                    + "x-acs-signature-nonce:" + nonce + "\n"
                    + "x-acs-version:" + API_VERSION + "\n";
            String signedHeaders = "host;x-acs-action;x-acs-content-sha256;x-acs-date;x-acs-signature-nonce;x-acs-version";

            String canonicalRequest = "POST\n/\n\n" + canonicalHeaders + "\n" + signedHeaders + "\n" + hashedPayload;
            String stringToSign = ALGORITHM + "\n" + sha256Hex(canonicalRequest);

            byte[] signatureBytes = hmacSha256(accessKeySecret.getBytes(StandardCharsets.UTF_8), stringToSign);
            String signature = bytesToHex(signatureBytes);

            String authorization = ALGORITHM + " Credential=" + accessKeyId
                    + ",SignedHeaders=" + signedHeaders + ",Signature=" + signature;

            HttpResponse httpResponse = HttpRequest.post(ENDPOINT)
                    .header("Content-Type", "application/json; charset=utf-8")
                    .header("Host", HOST)
                    .header("x-acs-action", action)
                    .header("x-acs-version", API_VERSION)
                    .header("x-acs-date", dateTime)
                    .header("x-acs-signature-nonce", nonce)
                    .header("x-acs-content-sha256", hashedPayload)
                    .header("Authorization", authorization)
                    .body(payload)
                    .timeout(TIMEOUT)
                    .execute();

            JSONObject json = JSON.parseObject(httpResponse.body());

            if (json.containsKey("Code") && !"200".equals(json.getString("Code"))) {
                log.error("阿里云 OCR 调用失败: action={}, code={}, message={}",
                        action, json.getString("Code"), json.getString("Message"));
                return null;
            }

            return json;
        } catch (Exception e) {
            log.error("阿里云 OCR 请求异常: action={}, error={}", action, e.getMessage(), e);
            return null;
        }
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
}
