package gptpro;


import org.eclipse.core.commands.ExecutionException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

import java.util.List;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.*;

import org.eclipse.swt.program.Program;

public class FindOpenAPIDraw {
   private String response;
   private Button searchButton;
   private Text searchText;
   private Button clearButton;
   private String extractedKeywords = ""; // 추출된 키워드를 저장하는 변수

   
   public void draw(Composite parent) {
      
      Label codeLabel = new Label(parent, SWT.NONE);
      codeLabel.setText("Enter OpenAPI content");
      
      searchText = new Text(parent, SWT.MULTI | SWT.BORDER | SWT.WRAP);
      GridData searchData = new GridData(SWT.LEFT, SWT.CENTER, true, false);
      searchData.widthHint = 500;
      searchData.heightHint = 50;
      searchText.setLayoutData(searchData);
      
      searchButton=  new Button(parent, SWT.PUSH);
      searchButton.setText("Search");
      GridData buttonData3 = new GridData(SWT.BOTTOM, SWT.CENTER, false, false);
      
      searchButton.setLayoutData(buttonData3);
      
     
      
      clearButton = new Button(parent, SWT.PUSH); // 추가된 부분
      clearButton.setText("CLEAR"); // 추가된 부분
      GridData buttonData5 = new GridData(SWT.TOP, SWT.TOP, false, false); // 추가된 부분
      clearButton.setLayoutData(buttonData5); // 추가된 부분
      clearButton.addListener(SWT.Selection, e -> { // 추가된 부분
          clearInputFields(); // 추가된 부분
      }); // 추가된 부분

      
      searchButton.addListener(SWT.Selection, e ->{
         System.out.println(searchText.getText());
          
          String keywords = TFIDFKeywordExtractor.extractKeywords(searchText.getText(), 5); // 5개의 키워드 추출
          extractedKeywords = keywords; // 추출된 키워드를 변수에 저장

          
         
         String selectedText = "{\"model\": \"gpt-3.5-turbo\", \"messages\": "
                 + "[{\"role\": \"system\", \"content\": \"Please suggest relevant Open API Link\\n\"},"
                 + "{\"role\": \"user\", \"content\": \"about " +extractedKeywords
                 + "\"}], \"max_tokens\": 500}";
      
       // 원래는 오픈소스로 하기로 하였지만 gpt-3.5-turbo의 한계로 인하여 open api로 바꾸어 개발하였다

         OpenAIHandler handler = new OpenAIHandler();
         
         try {
            response = (String) handler.callOpenAPI(selectedText);
           
            
         }catch(ExecutionException ex) {
                ex.printStackTrace();
            }
         
            openNewWindow3(response);
      
      
      
      
      });
   
   }
   
   public class TFIDFKeywordExtractor {
      
      private static final Set<String> STOP_WORDS = new HashSet<>(Arrays.asList(
              "a", "an", "the", "in", "on", "at", "of", "for", "to", "with", "is", "are", "am",
              "and", "or", "but", "not", "from", "by", "as", "was", "were", "it", "this", "that"
              // 추가적인 불용어를 필요에 따라 추가할 수 있습니다.
          ));
       // 문장에서 토큰(단어)을 추출하는 메서드
       private static String[] getTokens(String text) {
           return text.split("\\s+");
       }

       // 문장에서 단어의 빈도수를 계산하는 메서드
       private static Map<String, Integer> calculateTermFrequency(String text) {
           Map<String, Integer> termFrequency = new HashMap<>();
           String[] tokens = getTokens(text);
           for (String token : tokens) {
               termFrequency.put(token, termFrequency.getOrDefault(token, 0) + 1);
           }
           return termFrequency;
       }
       
       // 문서에서 단어가 나타나는 문서의 개수를 계산하는 메서드
       private static Map<String, Integer> calculateDocumentFrequency(List<String> documents) {
           Map<String, Integer> documentFrequency = new HashMap<>();
           for (String document : documents) {
               Set<String> uniqueTokens = new HashSet<>(Arrays.asList(getTokens(document)));
               for (String token : uniqueTokens) {
                   documentFrequency.put(token, documentFrequency.getOrDefault(token, 0) + 1);
               }
           }
           return documentFrequency;
       }
     

       // TF-IDF를 계산하여 가장 중요한 키워드를 추출하는 메서드
       public static String extractKeywords(String text, int numKeywords) {
           String[] sentences = text.split("[.!?]");
           List<String> documents = Arrays.asList(sentences);

           Map<String, Integer> termFrequency = calculateTermFrequency(text);
           Map<String, Integer> documentFrequency = calculateDocumentFrequency(documents);

           List<Map.Entry<String, Double>> tfidfEntries = new ArrayList<>();
           for (String token : termFrequency.keySet()) {
               if (!STOP_WORDS.contains(token.toLowerCase())) { // 불용어인 경우 제외
                   double tfidf = (double) termFrequency.get(token) * Math.log((double) documents.size() / (documentFrequency.get(token) + 1));
                   tfidfEntries.add(new AbstractMap.SimpleEntry<>(token, tfidf));
               }
           }
           tfidfEntries.sort((entry1, entry2) -> Double.compare(entry2.getValue(), entry1.getValue()));

           // 상위 numKeywords개의 키워드 추출
           StringBuilder keywords = new StringBuilder();
           for (int i = 0; i < Math.min(numKeywords, tfidfEntries.size()); i++) {
               String keyword = tfidfEntries.get(i).getKey();
               keywords.append(keyword).append(", ");
           }

           return keywords.toString();
       }
   }
   
   private void openNewWindow3(String response) {
       Shell newShell3 = new Shell(Display.getDefault());
       newShell3.setText("suggestion");
       newShell3.setLayout(new GridLayout(1, false));

       // 링크를 출력하기 위해 Link 위젯 생성
       Link infoLink = new Link(newShell3, SWT.WRAP);
       infoLink.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

       // 응답에서 링크를 찾아 파란색으로 표시하고 클릭 이벤트를 추가합니다.
       parseResponseForLinks(response, infoLink);

       newShell3.setSize(500, 700);
       newShell3.open();
   }

   private void parseResponseForLinks(String response, Link link) {
       // Clear any existing content in the Link widget
       link.setText("");

       // Define a regular expression pattern for matching URLs
       String urlPattern = "(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
       Pattern pattern = Pattern.compile(urlPattern);
       Matcher matcher = pattern.matcher(response);

       // Iterate through the matched URLs and replace them with clickable links
       StringBuffer linkedResponse = new StringBuffer();
       while (matcher.find()) {
           String url = matcher.group();
           String linkedText = "<a>" + url + "</a>";
           matcher.appendReplacement(linkedResponse, linkedText);
       }
       matcher.appendTail(linkedResponse);

       if (linkedResponse.length() > 0) {
           // Set the modified text with clickable links to the Link widget
           link.setText(linkedResponse.toString());

           // Add the SelectionListener to handle link clicks
           link.addSelectionListener(new SelectionAdapter() {
               public void widgetSelected(SelectionEvent e) {
                   openLinkInBrowser(e.text);
               }
           });
       } else {
           // If no links are found, display the original response in the Link widget
           link.setText(response);
           link.setForeground(new Color(Display.getDefault(), 0, 0, 0));
       }
   }


   private void openLinkInBrowser(String linkText) {
       String url = linkText.replaceAll("<a>|</a>", "");

       // Program 클래스를 사용하여 기본 웹 브라우저를 엽니다.
       Program.launch(url);
   }
   private void clearInputFields() {
      searchText.setText("");
   }
}
   