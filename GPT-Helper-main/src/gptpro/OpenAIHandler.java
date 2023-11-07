package gptpro;


import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;


public class OpenAIHandler extends AbstractHandler {	

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {

        return null;
    }
    
    public String callOpenAPI(String requestBody) throws ExecutionException {
        // OpenAI API 인증 정보
        String apiKey = "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";

        // 요청할 엔드포인트 URL
        String apiUrl = "https://api.openai.com/v1/chat/completions";

        // HttpClient 생성
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            // HTTP POST 요청 생성
            HttpPost request = new HttpPost(apiUrl);

            // HTTP 헤더 설정
            request.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey);
            request.setHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType());

            // 요청 본문 설정
            StringEntity entity = new StringEntity(requestBody, ContentType.APPLICATION_JSON);
            request.setEntity(entity);

            // API 호출 및 응답 처리
            HttpResponse response = httpClient.execute(request);
            HttpEntity responseEntity = response.getEntity();

            // 응답 본문 출력
            if (responseEntity != null) {
                String responseBody = EntityUtils.toString(responseEntity);
                
                // JSON 파싱
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode jsonNode = objectMapper.readTree(responseBody);

                // choices 배열 확인
                JsonNode choicesNode = jsonNode.get("choices");
                if (choicesNode != null && choicesNode.isArray() && choicesNode.size() > 0) {
                    // 첫 번째 메시지 추출
                    JsonNode firstChoiceNode = choicesNode.get(0);
                    JsonNode messageNode = firstChoiceNode.get("message");

                    if (messageNode != null && messageNode.has("content")) {
                        // content 값 추출
                        String content = messageNode.get("content").asText();

                        System.out.println(content);
                        return content; // 응답 본문을 반환합니다.
                    }
                    System.out.println(choicesNode);
                    return "not valid API key";
                }
                System.out.println(responseBody);
                return "not valid API key";
            }
        } catch (Exception e) {
            e.printStackTrace();
            // 예외 처리 로직 작성
        }

        return null;
    }
    
    public static String escapeJson(String input) {
        StringBuilder builder = new StringBuilder();

        for (char c : input.toCharArray()) {
            switch (c) {
                case '\"':
                    builder.append("\\\"");
                    break;
                case '\\':
                    builder.append("\\\\");
                    break;
                case '/':
                    builder.append("\\/");
                    break;
                case '\b':
                    builder.append("\\b");
                    break;
                case '\f':
                    builder.append("\\f");
                    break;
                case '\n':
                    builder.append("\\n");
                    break;
                case '\r':
                    builder.append("\\r");
                    break;
                case '\t':
                    builder.append("\\t");
                    break;
                default:
                    builder.append(c);
                    break;
            }
        }

        return builder.toString();
    }

}
