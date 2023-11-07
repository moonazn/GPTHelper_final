package gptpro;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.swt.widgets.Display;

public class PartCoding {
	
	static String outputStr;
	
	public void partCoding(){
		
		// 요구사항 추출 
		extractStr();
		
		String escapedCode = OpenAIHandler.escapeJson(outputStr);
        
        String selectedText = "{\"model\": \"gpt-3.5-turbo\", \"messages\": "
              + "[{\"role\": \"system\", \"content\": \"Create java codes about this.\"},"
              + "{\"role\": \"user\", \"content\": \"" + escapedCode
              + "\"}], \"max_tokens\": 500}";
        
        OpenAIHandler handler = new OpenAIHandler();

        try {
            String reponse = (String) handler.callOpenAPI(selectedText); 
            if (reponse != null) {
                Display.getDefault().asyncExec(() -> {
                    if (RequireDraw.codeText != null && !RequireDraw.codeText.isDisposed()) {
                       System.out.print("yes");
                       RequireDraw.codeText.setText(reponse);
                    }
                });
            }
        } catch (ExecutionException ex) {
            ex.printStackTrace();
        }
	}
	
	private void extractStr() {

		// 클릭된 문자열로부터 특정 문자열까지의 부분 문자열을 추출하는 정규식 패턴
		// 			클릭된 텍스트 이후의 문자열을 검색하여 특정 문자열이 존재하는지 확인
	    String textToSearch = RequireDraw.responseText.getText().substring(RequireDraw.responseText.getText()
	    		.indexOf(RequireDraw.clickedText) + RequireDraw.clickedText.length());
	    String targetString = "요구사항 ID"; // 여기에 원하는 특정 문자열을 입력
	    String patternStr;
	    
	    if (textToSearch.contains(targetString)) {
	    	// 처음~중간 요구사항일 경우 
		    patternStr = Pattern.quote(RequireDraw.clickedText) + "(.*?)" + Pattern.quote("요구사항 ID") + "(.*?)";

	    } else {
	    	// 마지막 요구사항일 경우
	    	patternStr = Pattern.quote(RequireDraw.clickedText) + "(.*)";

	    } 

	    // 세부사항 추출을 위한 패턴
	    Pattern detailPattern = Pattern.compile(patternStr, Pattern.DOTALL);
	    Matcher detailMatcher = detailPattern.matcher(RequireDraw.responseText.getText());
	    
	    while(detailMatcher.find()) {
        	outputStr = detailMatcher.group(1);
        }
	    
	    System.out.println(outputStr);
	}
}
