package gptpro;


import org.eclipse.core.commands.ExecutionException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

public class ExplainDraw {
	
	private String response;

    private Button submitButton2;
    private Button translationButton;
    private Button clearButton; // 추가된 부분

    private Text codeText;

	
	public void draw(Composite parent) {
		// Explain 탭에 추가 텍스트 입력 필드 생성

        Label codeLabel = new Label(parent, SWT.NONE);
        codeLabel.setText("Please enter the code:");

        codeText = new Text(parent, SWT.MULTI | SWT.BORDER | SWT.WRAP);
        GridData codeData = new GridData(SWT.FILL, SWT.CENTER, true, false);
        codeData.widthHint = 500; // 텍스트 입력공간의 좌우 길이 조정
        codeData.heightHint = 500;
        codeText.setLayoutData(codeData);

        submitButton2 = new Button(parent, SWT.PUSH);
        submitButton2.setText("SUBMIT");
        GridData buttonData2 = new GridData(SWT.BOTTOM, SWT.CENTER, false, false);
        
        submitButton2.setLayoutData(buttonData2);
        
        submitButton2.addListener(SWT.Selection, e -> {
        	
        	String escapedCode = OpenAIHandler.escapeJson(codeText.getText());
        	
        	String selectedText = "{\"model\": \"gpt-3.5-turbo\", \"messages\": "
              		+ "[{\"role\": \"system\", \"content\": \"Analyze the code.\"},"
              		+ "{\"role\": \"user\", \"content\": \"code: " + escapedCode
              		+ "\"}], \"max_tokens\": 500}";
            
            OpenAIHandler handler = new OpenAIHandler();

            try {
                response = (String) handler.callOpenAPI(selectedText); 
            } catch (ExecutionException ex) {
                ex.printStackTrace();
            }
            
            openNewWindow2();
          });
        
        clearButton = new Button(parent, SWT.PUSH); // 추가된 부분
        clearButton.setText("CLEAR"); // 추가된 부분
        GridData buttonData5 = new GridData(SWT.TOP, SWT.TOP, false, false); // 추가된 부분
        clearButton.setLayoutData(buttonData5); // 추가된 부분
        clearButton.addListener(SWT.Selection, e -> { // 추가된 부분
            clearInputFields(); // 추가된 부분
        }); // 추가된 부분

	}
	
	private void openNewWindow2() {
        Shell newShell2 = new Shell(Display.getDefault());
        newShell2.setText("EXPLAIN");
        newShell2.setLayout(new GridLayout(1, false));

        // 다른 곳에서 받아온 정보를 출력할 Label 생성
        Label infoLabel = new Label(newShell2, SWT.WRAP);
        infoLabel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        // 다른 곳에서 받아온 정보를 출력하기 위해 해당 정보를 변수에 저장한 뒤 setText() 메서드로 Label에 설정
        

        
        if (response != null) {
            Display.getDefault().asyncExec(() -> {
                if (infoLabel != null && !infoLabel.isDisposed()) {
                    System.out.print("yes");
                    infoLabel.setText(response);
                }
            });
        }
          

        translationButton = new Button(newShell2, SWT.PUSH);
        translationButton.setText("TRANSLATE");
        GridData buttonData4 = new GridData(SWT.CENTER, SWT.BEGINNING, false, false);
        translationButton.setLayoutData(buttonData4);
        
        translationButton.addListener(SWT.Selection, e -> {
        	String escapedText = OpenAIHandler.escapeJson(infoLabel.getText());
        	String selectedText = "{\"model\": \"gpt-3.5-turbo\", \"messages\": "
              		+ "[{\"role\": \"system\", \"content\": \"Translate into Korean.\"},"
              		+ "{\"role\": \"user\", \"content\": \" " + escapedText
              		+ "\"}], \"max_tokens\": 500}";
        	
        	OpenAIHandler handler = new OpenAIHandler();

            try {
                String reponse = (String) handler.callOpenAPI(selectedText); 
                if (reponse != null) {
                    Display.getDefault().asyncExec(() -> {
                        if (infoLabel != null && !infoLabel.isDisposed()) {
                        	System.out.print("yes");
                            infoLabel.setText(reponse);
                        }
                    });
                }
            } catch (ExecutionException ex) {
                ex.printStackTrace();
            }
          });
        
        newShell2.setSize(500, 700);
        newShell2.open();
    }

	private void clearInputFields() {
        codeText.setText("");
    }

	
}
