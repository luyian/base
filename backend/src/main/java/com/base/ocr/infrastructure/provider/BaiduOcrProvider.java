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
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.concurrent.TimeUnit;

/**
 * 百度云 OCR 识别提供者
 * <p>
 * access_token 缓存到 Redis，有效期 30 天（百度默认），提前 1 天刷新。
 * </p>
 *
 * @author base
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class BaiduOcrProvider implements OcrProvider {

    private static final String TOKEN_URL = "https://aip.baidubce.com/oauth/2.0/token";
    private static final String ID_CARD_URL = "https://aip.baidubce.com/rest/2.0/ocr/v1/idcard";
    private static final String INVOICE_URL = "https://aip.baidubce.com/rest/2.0/ocr/v1/vat_invoice";
    private static final String BANK_CARD_URL = "https://aip.baidubce.com/rest/2.0/ocr/v1/bankcard";
    private static final String REDIS_TOKEN_KEY = "ocr:baidu:access_token";
    private static final long TOKEN_EXPIRE_DAYS = 29;
    private static final int TIMEOUT = 30000;

    private final OcrSourceConfig config;
    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public String getName() {
        return "baidu";
    }

    @Override
    public String getDisplayName() {
        return "百度云";
    }

    @Override
    public IdCardResult recognizeIdCard(byte[] imageData, String side) {
        String accessToken = getAccessToken();
        if (accessToken == null) {
            return null;
        }

        String imageBase64 = Base64.getEncoder().encodeToString(imageData);
        String idCardSide = "front".equalsIgnoreCase(side) ? "front" : "back";
        String body = "image=" + urlEncode(imageBase64) + "&id_card_side=" + idCardSide;

        JSONObject response = callApi(ID_CARD_URL, accessToken, body);
        if (response == null) {
            return null;
        }

        JSONObject wordsResult = response.getJSONObject("words_result");
        if (wordsResult == null) {
            return null;
        }

        IdCardResult result = new IdCardResult();
        result.setSide(side);
        result.setProvider(getName());

        if ("front".equalsIgnoreCase(side)) {
            result.setName(getWords(wordsResult, "姓名"));
            result.setGender(getWords(wordsResult, "性别"));
            result.setNation(getWords(wordsResult, "民族"));
            result.setBirthday(getWords(wordsResult, "出生"));
            result.setAddress(getWords(wordsResult, "住址"));
            result.setIdNumber(getWords(wordsResult, "公民身份号码"));
        } else {
            result.setAuthority(getWords(wordsResult, "签发机关"));
            result.setValidDateStart(getWords(wordsResult, "签发日期"));
            result.setValidDateEnd(getWords(wordsResult, "失效日期"));
        }

        return result;
    }

    @Override
    public InvoiceResult recognizeInvoice(byte[] imageData) {
        String accessToken = getAccessToken();
        if (accessToken == null) {
            return null;
        }

        String imageBase64 = Base64.getEncoder().encodeToString(imageData);
        String body = "image=" + urlEncode(imageBase64);

        JSONObject response = callApi(INVOICE_URL, accessToken, body);
        if (response == null) {
            return null;
        }

        JSONObject wordsResult = response.getJSONObject("words_result");
        if (wordsResult == null) {
            return null;
        }

        InvoiceResult result = new InvoiceResult();
        result.setProvider(getName());
        result.setInvoiceType(getWords(wordsResult, "InvoiceType"));
        result.setInvoiceCode(getWords(wordsResult, "InvoiceCode"));
        result.setInvoiceNumber(getWords(wordsResult, "InvoiceNum"));
        result.setInvoiceDate(getWords(wordsResult, "InvoiceDate"));
        result.setCheckCode(getWords(wordsResult, "CheckCode"));
        result.setBuyerName(getWords(wordsResult, "PurchaserName"));
        result.setBuyerTaxId(getWords(wordsResult, "PurchaserRegisterNum"));
        result.setSellerName(getWords(wordsResult, "SellerName"));
        result.setSellerTaxId(getWords(wordsResult, "SellerRegisterNum"));
        result.setTotalAmount(getWords(wordsResult, "TotalAmount"));
        result.setTotalTax(getWords(wordsResult, "TotalTax"));
        result.setAmountInWords(getWords(wordsResult, "AmountInWords"));
        result.setAmountInFigures(getWords(wordsResult, "AmountInFiguers"));

        return result;
    }

    @Override
    public BankCardResult recognizeBankCard(byte[] imageData) {
        String accessToken = getAccessToken();
        if (accessToken == null) {
            return null;
        }

        String imageBase64 = Base64.getEncoder().encodeToString(imageData);
        String body = "image=" + urlEncode(imageBase64);

        JSONObject response = callApi(BANK_CARD_URL, accessToken, body);
        if (response == null) {
            return null;
        }

        JSONObject resultJson = response.getJSONObject("result");
        if (resultJson == null) {
            return null;
        }

        BankCardResult result = new BankCardResult();
        result.setProvider(getName());
        result.setCardNumber(resultJson.getString("bank_card_number"));
        result.setBankName(resultJson.getString("bank_name"));
        String cardType = resultJson.getString("bank_card_type");
        if ("1".equals(cardType)) {
            result.setCardType("借记卡");
        } else if ("2".equals(cardType)) {
            result.setCardType("信用卡");
        }
        result.setValidDate(resultJson.getString("valid_date"));
        result.setHolderName(resultJson.getString("holder_name"));

        return result;
    }

    /**
     * 获取百度云 access_token（Redis 缓存）
     */
    private String getAccessToken() {
        String token = stringRedisTemplate.opsForValue().get(REDIS_TOKEN_KEY);
        if (token != null && !token.isEmpty()) {
            return token;
        }

        String apiKey = config.getBaiduApiKey();
        String secretKey = config.getBaiduSecretKey();
        if (apiKey.isEmpty() || secretKey.isEmpty()) {
            log.warn("百度云 OCR ApiKey/SecretKey 未配置");
            return null;
        }

        try {
            String url = TOKEN_URL + "?grant_type=client_credentials&client_id=" + apiKey + "&client_secret=" + secretKey;
            HttpResponse response = HttpRequest.post(url)
                    .timeout(10000)
                    .execute();

            JSONObject json = JSON.parseObject(response.body());
            token = json.getString("access_token");

            if (token == null || token.isEmpty()) {
                log.error("百度云 OCR 获取 access_token 失败: {}", response.body());
                return null;
            }

            stringRedisTemplate.opsForValue().set(REDIS_TOKEN_KEY, token, TOKEN_EXPIRE_DAYS, TimeUnit.DAYS);
            log.info("百度云 OCR access_token 刷新成功");
            return token;
        } catch (Exception e) {
            log.error("百度云 OCR 获取 access_token 异常: {}", e.getMessage(), e);
            return null;
        }
    }

    private JSONObject callApi(String url, String accessToken, String body) {
        try {
            String fullUrl = url + "?access_token=" + accessToken;
            HttpResponse response = HttpRequest.post(fullUrl)
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .body(body)
                    .timeout(TIMEOUT)
                    .execute();

            JSONObject json = JSON.parseObject(response.body());

            if (json.containsKey("error_code")) {
                log.error("百度云 OCR 调用失败: error_code={}, error_msg={}",
                        json.getString("error_code"), json.getString("error_msg"));
                return null;
            }

            return json;
        } catch (Exception e) {
            log.error("百度云 OCR 请求异常: url={}, error={}", url, e.getMessage(), e);
            return null;
        }
    }

    private String getWords(JSONObject wordsResult, String key) {
        JSONObject field = wordsResult.getJSONObject(key);
        if (field == null) {
            return null;
        }
        return field.getString("words");
    }

    private String urlEncode(String value) {
        try {
            return URLEncoder.encode(value, StandardCharsets.UTF_8.name());
        } catch (Exception e) {
            return value;
        }
    }
}
