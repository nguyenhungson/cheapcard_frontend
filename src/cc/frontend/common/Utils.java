/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cc.frontend.common;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.twmacinta.util.MD5;
import hapax.Template;
import hapax.TemplateDataDictionary;
import hapax.TemplateException;
import hapax.TemplateLoader;
import hapax.TemplateResourceLoader;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.log4j.Logger;

/**
 *
 * @author sonnh4
 */
public class Utils {

    private static Logger logger = Logger.getLogger(Utils.class);
    private static String staticURL = TGRConfig.gStaticURL;
    private static final String _validChars = "QWERTYUIOPLKJHGFDSAZXCVBNM0125634789";
    private static final int _length = _validChars.length();

    public static void prepareHeader(HttpServletResponse resp) {
        resp.setCharacterEncoding("UTF-8");
        resp.addHeader("Content-Type", "text/html;charset=UTF-8");
        resp.addHeader("Server", "ZWS");
    }

    public static void prepareHeaderAjax(HttpServletResponse resp, String content) {
        resp.setCharacterEncoding("UTF-8");
        resp.addHeader("Content-Type", "application/json;charset=UTF-8");
    }

    public static void out(String content, HttpServletResponse respon) {
        Utils.prepareHeader(respon);
        PrintWriter out = null;
        try {
            out = respon.getWriter();
            if (respon.getStatus() == HttpServletResponse.SC_OK) {
                out.print(content);
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }

    public static void outAjax(String content, HttpServletResponse respon) {
        Utils.prepareHeaderAjax(respon, content);
        PrintWriter out = null;
        try {
            out = respon.getWriter();
            if (respon.getStatus() == HttpServletResponse.SC_OK) {
                logger.info(" >> sau: " + content.length());
                out.print(content);
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }

    public static String readFile(String fileName) {

        BufferedReader br = null;
        String strFile = "";
        try {
            br = new BufferedReader(new FileReader(fileName));
            String sCurrentLine = "";
            while ((sCurrentLine = br.readLine()) != null) {
                strFile += sCurrentLine + "\n";
            }

        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (IOException ex) {
                logger.error(ex.getMessage(), ex);
            }
        }

        return strFile;

    }

    public static String readAllFile(String fileName) {
        File file = new File(fileName);
        String strFile = "";
        try {
            strFile = FileUtils.readFileToString(file);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }

        return strFile;
    }

    public static String renderTemplate(String fileTemplate, TemplateDataDictionary myDic) throws TemplateException {
        TemplateLoader templateLoader = TemplateResourceLoader.create("");
        String fileNameTemplate = fileTemplate.split("/")[1];
        String keyCache = KeyCacheConfig.template + fileNameTemplate;
        Object cacheContent = LocalCache.getInstance().getObjectCache(keyCache);
        String content;
        if (cacheContent == null) {
            content = Utils.readFile(fileTemplate);
            //LocalCache.getInstance().setObjectCache(keyCache, content, 0);
        } else {
            content = cacheContent.toString();
        }
        Template template = new Template(content, templateLoader);
        myDic.setVariable("static_url", staticURL);
        myDic.setVariable("static_version", TGRConfig.timeDeploy);

        return template.renderToString(myDic);
    }
    
    public static String renderTemplateMasterpage(String mainContent, TemplateDataDictionary myDic) throws Exception{
        myDic.setVariable("main_content", mainContent);
        String masterPage = Utils.renderTemplate("Template/masterpage.html", myDic);
        return masterPage;
    }
    
    public static String render404Page(TemplateDataDictionary myDic) throws Exception{
        String masterPage = Utils.renderTemplate("Template/404.html", myDic);
        return masterPage;
    }

    public static String getClientIP(HttpServletRequest req) {
        String ipAddress = req.getHeader("X-FORWARDED-FOR");
        if (ipAddress == null) {
            ipAddress = req.getRemoteAddr();
        }

        return ipAddress;
    }

    public static String getServerIP() {

        InetAddress ip;
        try {

            ip = InetAddress.getLocalHost();
            return ip.getHostAddress();

        } catch (UnknownHostException e) {

            logger.error(e.getMessage(), e);

        }

        return "";

    }

    public static String encryptMD5(String input) {
        MD5 md = new MD5(input);
        return md.asHex();
    }

    public static String encryptSHA1(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            byte[] hash = digest.digest(input.getBytes("UTF-8"));
            StringBuilder hexString = new StringBuilder();

            for (int i = 0; i < hash.length; i++) {
                String hex = Integer.toHexString(0xff & hash[i]);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            return null;
        }
    }

    public static String encryptSHA256(String input) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(input.getBytes("UTF-8"));
        StringBuilder hexString = new StringBuilder();

        for (int i = 0; i < hash.length; i++) {
            String hex = Integer.toHexString(0xff & hash[i]);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }

        return hexString.toString();
    }

    public static String convertZingIDToAccountCode(String uin) {
        int length = uin.length();
        String accountCode = uin;

        if (length < 10) {
            for (int i = 0; i < 10 - length; i++) {
                if (i == (9 - length)) {
                    accountCode = "9" + accountCode;
                } else {
                    accountCode = "0" + accountCode;
                }
            }
        }

        return accountCode;
    }

    public static String formatNumber(Object number) {
        Locale locale = new Locale("en", "UK");

        DecimalFormatSymbols symbols = new DecimalFormatSymbols(locale);
        symbols.setDecimalSeparator(',');
        symbols.setGroupingSeparator('.');

        DecimalFormat df1 = new DecimalFormat("#,###.#", symbols);

        return df1.format(number);
    }

    public static String renderPaging(String urlInfo, int numberOfDetail, int pageSize, int currentPage, String plusParam) {
        int numberOfPage = (int) Math.ceil((double) numberOfDetail / pageSize);
        int prevPage = currentPage == 1 ? 1 : currentPage - 1;
        int nextPage = currentPage == numberOfPage ? numberOfPage : currentPage + 1;
        int lastPage = numberOfPage;
        int startPaging = currentPage - 2 < 1 ? 1 : currentPage - 2;
        int endPaging = currentPage + 2 > numberOfPage ? numberOfPage : currentPage + 2;
        String html = "";

        if (currentPage == 1) {
            html = "<li><a href='javascript:;' class='disable'>Đầu</a></li>"
                    + "<li><a href='javascript:;' class='disable'>Trước</a></li>";
        } else {
            html = "<li><a href='" + urlInfo + ".1" + plusParam + ".html' class='first'>Đầu</a></li>"
                    + "<li><a href='" + urlInfo + "." + prevPage + plusParam + ".html'>Trước</a></li>";
        }

        for (int i = startPaging; i <= endPaging; i++) {
            if (i == currentPage) {
                html += "<li><a href='javascript:;' class='active'>" + i + "</a></li>";
            } else {
                html += "<li><a href='" + urlInfo + "." + i + plusParam + ".html'>" + i + "</a></li>";
            }
        }

        if (currentPage == lastPage) {
            html += "<li><a href='javascript:;' class='disable'>Tiếp</a></li>"
                    + "<li><a href='javascript:;' class='disable'>Cuối</a></li>";
        } else {
            html += "<li><a href='" + urlInfo + "." + nextPage + plusParam + ".html'>Tiếp</a></li>"
                    + "<li><a href='" + urlInfo + "." + lastPage + plusParam + ".html'>Cuối</a></li>";
        }

        return html;
    }

    public static String getStringTime(long time, String format) {
        String _rs = "";
        Date d = new Date(time);
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        _rs = sdf.format(d);
        return _rs;
    }

    public static String getToday(String format) {
        return getStringTime(System.currentTimeMillis(), format);
    }

    public static String getTodayOfLastMonth(String format) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, -1);
        return getStringTime(cal.getTimeInMillis(), format);
    }

    public static String getThreeMonthAgo(String format) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, -3);
        return getStringTime(cal.getTimeInMillis(), format);
    }

    public static String getUTCDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("MMddyyHHmmss");
        return sdf.format(new Date());
    }

    public static String removeVietnamese(String strOrigin) {
        String strDesc = Normalizer.normalize(strOrigin, Normalizer.Form.NFD);
        String sourceText[] = {"đ", "Đ"};
        String descText[] = {"d", "D"};
        for (int i = 0; i < sourceText.length; i++) {
            strDesc = strDesc.replaceAll(sourceText[i], descText[i]);
        }

        strDesc = strDesc.replaceAll("[^\\x00-\\x7F]", "");

        return strDesc;
    }

    public static String getUserBrowser(HttpServletRequest request) {
        String result = "";
        String userAgent = request.getHeader("User-Agent");
        userAgent = userAgent.toLowerCase();
        if (userAgent.contains("chrome")) {
            result = "Chrome";
        } else if (userAgent.contains("firefox")) {
            result = "Firefox";
        } else {
            result = "MSIE";
        }

        return result;
    }

    public static JsonObject callAPIRestJsonObject(String apiUrl, String[] paramsKey, String[] paramsValue) throws Exception {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(apiUrl);
        JsonObject json = new JsonObject();
        int length = paramsKey.length;
        for (int i = 0; i < length; i++) {
            json.addProperty(paramsKey[i], paramsValue[i]);
        }
        httpPost.setEntity(new StringEntity(json.toString()));
        Date now = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        httpPost.setHeader("request_date", formatter.format(now));
        try {
            CloseableHttpResponse res = httpclient.execute(httpPost);
            HttpEntity entity = res.getEntity();

            InputStream inputStream = entity.getContent();
            String sResponse = IOUtils.toString(inputStream, "UTF-8");

            JsonElement jsonElement = new JsonParser().parse(sResponse);
            JsonObject jsonObj = jsonElement.getAsJsonObject();

            return jsonObj;
        } catch (Exception ex) {
            logger.error(ex.getMessage());
        }

        return null;
    }

    public static String sendHTTPGet(String url) throws Exception {
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        
        return response.toString();
    }

    public static String callAPIRestObject(String apiUrl, String[] paramsKey, String[] paramsValue) throws Exception {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(apiUrl);
        JsonObject json = new JsonObject();
        int length = paramsKey.length;
        for (int i = 0; i < length; i++) {
            json.addProperty(paramsKey[i], paramsValue[i]);
        }
        httpPost.setEntity(new StringEntity(json.toString(), "UTF-8"));
        Date now = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        httpPost.setHeader("request_date", formatter.format(now));
        try {
            CloseableHttpResponse res = httpclient.execute(httpPost);
            HttpEntity entity = res.getEntity();

            InputStream inputStream = entity.getContent();
            String sResponse = IOUtils.toString(inputStream, "UTF-8");

            return sResponse;
        } catch (Exception ex) {
            logger.error(ex.getMessage());
        }

        return null;
    }
    
    public static String callAPIRestObject(String url, Object req) throws Exception{
        String result = "";
        
        RequestConfig requestConfig = RequestConfig.custom()
                .setSocketTimeout(90000)
                .setConnectTimeout(90000)
                .build();
        CloseableHttpClient httpclient = HttpClientBuilder.create().setDefaultRequestConfig(requestConfig).build();
        HttpPost httpPost = new HttpPost(url);
        httpPost.setHeader("Content-Type", "application/json;charset=UTF-8");
        httpPost.setHeader("Charset", "UTF-8");
        
        httpPost.setEntity(new StringEntity(new Gson().toJson(req)));
        try (CloseableHttpResponse res = httpclient.execute(httpPost)) {
            int statusCode = res.getStatusLine().getStatusCode();
            if (statusCode == HttpStatus.SC_OK) {
                HttpEntity entity = res.getEntity();
                InputStream inputStream = entity.getContent();
                result = IOUtils.toString(inputStream, "UTF-8");
            }
        }
        
        return result;
    }

    public static JsonObject convertStringToJsonObject(String data) {
        JsonElement jsonElement = new JsonParser().parse(data);
        JsonObject jsonObj = jsonElement.getAsJsonObject();

        return jsonObj;
    }

    public static String convertIntToString(int number, int lenRequired) {
        String result = "";
        try {
            if (number == 0) {
                result += _validChars.charAt(0);
            } else {
                while (number > 0) {
                    result = _validChars.charAt(number % _length) + result;
                    number = number / _length;
                }
            }

            if (result.length() < lenRequired) {
                int pad = lenRequired - result.length();
                for (int c = 0; c < pad; c++) {
                    result = _validChars.charAt(0) + result;
                }
            }
        } catch (Exception ex) {
            logger.error(String.format("convertIntToString >> Error: %s", ex));
        }
        return result;
    }

    public static String separatorPhoneNumber(String phoneNumber) {
        String newPhone = "";
        int index = phoneNumber.length() - 1;
        int position = 0;
        while (position < 8) {
            if (position == 3 || position == 7) {
                newPhone = "." + newPhone;
            } else {
                newPhone = phoneNumber.charAt(index) + newPhone;
                index--;
            }
            position++;
        }
        newPhone = phoneNumber.substring(0, index + 1) + newPhone;
        return newPhone;
    }

    public static String sha256(String input) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(input.getBytes("UTF-8"));
        StringBuilder hexString = new StringBuilder();

        for (int i = 0; i < hash.length; i++) {
            String hex = Integer.toHexString(0xff & hash[i]);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }

        return hexString.toString();
    }

    public static char findVietnamese(char ch) {

        char[] SPECIAL_CHARACTERS = {'À', 'Á', 'Â', 'Ã', 'È', 'É', 'Ê', 'Ì', 'Í', 'Ò',
            'Ó', 'Ô', 'Õ', 'Ù', 'Ú', 'Ý', 'à', 'á', 'â', 'ã', 'è', 'é', 'ê',
            'ì', 'í', 'ò', 'ó', 'ô', 'õ', 'ù', 'ú', 'ý', 'Ă', 'ă', 'Đ', 'đ',
            'Ĩ', 'ĩ', 'Ũ', 'ũ', 'Ơ', 'ơ', 'Ư', 'ư', 'Ạ', 'ạ', 'Ả', 'ả', 'Ấ',
            'ấ', 'Ầ', 'ầ', 'Ẩ', 'ẩ', 'Ẫ', 'ẫ', 'Ậ', 'ậ', 'Ắ', 'ắ', 'Ằ', 'ằ',
            'Ẳ', 'ẳ', 'Ẵ', 'ẵ', 'Ặ', 'ặ', 'Ẹ', 'ẹ', 'Ẻ', 'ẻ', 'Ẽ', 'ẽ', 'Ế',
            'ế', 'Ề', 'ề', 'Ể', 'ể', 'Ễ', 'ễ', 'Ệ', 'ệ', 'Ỉ', 'ỉ', 'Ị', 'ị',
            'Ọ', 'ọ', 'Ỏ', 'ỏ', 'Ố', 'ố', 'Ồ', 'ồ', 'Ổ', 'ổ', 'Ỗ', 'ỗ', 'Ộ',
            'ộ', 'Ớ', 'ớ', 'Ờ', 'ờ', 'Ở', 'ở', 'Ỡ', 'ỡ', 'Ợ', 'ợ', 'Ụ', 'ụ',
            'Ủ', 'ủ', 'Ứ', 'ứ', 'Ừ', 'ừ', 'Ử', 'ử', 'Ữ', 'ữ', 'Ự', 'ự', 'ỳ', 'Ỳ'};

        char[] REPLACEMENTS = {'A', 'A', 'A', 'A', 'E', 'E', 'E',
            'I', 'I', 'O', 'O', 'O', 'O', 'U', 'U', 'Y', 'a', 'a', 'a', 'a',
            'e', 'e', 'e', 'i', 'i', 'o', 'o', 'o', 'o', 'u', 'u', 'y', 'A',
            'a', 'D', 'd', 'I', 'i', 'U', 'u', 'O', 'o', 'U', 'u', 'A', 'a',
            'A', 'a', 'A', 'a', 'A', 'a', 'A', 'a', 'A', 'a', 'A', 'a', 'A',
            'a', 'A', 'a', 'A', 'a', 'A', 'a', 'A', 'a', 'E', 'e', 'E', 'e',
            'E', 'e', 'E', 'e', 'E', 'e', 'E', 'e', 'E', 'e', 'E', 'e', 'I',
            'i', 'I', 'i', 'O', 'o', 'O', 'o', 'O', 'o', 'O', 'o', 'O', 'o',
            'O', 'o', 'O', 'o', 'O', 'o', 'O', 'o', 'O', 'o', 'O', 'o', 'O',
            'o', 'U', 'u', 'U', 'u', 'U', 'u', 'U', 'u', 'U', 'u', 'U', 'u',
            'U', 'u', 'y', 'Y'};

        int index = Arrays.binarySearch(SPECIAL_CHARACTERS, ch);
        if (index >= 0) {
            ch = REPLACEMENTS[index];
        }
        return ch;
    }

    public static boolean isLong(String input) {
        try {
            Long.parseLong(input);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static int validateDate(Date fromDateInput, Date toDateInput) {

        Calendar cal = Calendar.getInstance();
        cal.setTime(fromDateInput);
        int fromDay = cal.get(Calendar.DAY_OF_MONTH),
                fromMonth = cal.get(Calendar.MONTH) + 1,
                fromYear = cal.get(Calendar.YEAR);
        Date currentDate = new Date();
        cal.setTime(currentDate);
        int currentDay = cal.get(Calendar.DAY_OF_MONTH),
                currentMonth = cal.get(Calendar.MONTH) + 1,
                currentYear = cal.get(Calendar.YEAR);
        if ((currentYear == fromYear) && (((currentMonth - fromMonth == 3) && (currentDay <= fromDay)) || (currentMonth - fromMonth < 3))) {
            return 0;
        } else if ((currentYear - fromYear == 1) && (((currentMonth - (fromMonth - 12) == 3) && (currentDay <= fromDay)) || (currentMonth - (fromMonth - 12) < 3))) {
            return 0;
        } else {
            return -12;
        }
    }

    public static void urlRedirect(HttpServletResponse resp, String url) {
        try {
            resp.sendRedirect(url);
            return;
        } catch (Exception ex) {
            logger.error("Not redirect URL: " + url);
        }
    }

}
