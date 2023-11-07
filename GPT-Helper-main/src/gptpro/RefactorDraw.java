package gptpro;

import java.io.InputStream;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class RefactorDraw {
   
   private Composite changeComposite;
    private Text beforeText;
    private Text afterText;
    private Text purposeText;
    private Text codeText;
    private Button changeButton;
    private Button copyButton;
    private Button clearButton; // 추가된 부분
    
    private String response;
    private Image doneImage;
    public static String clickedText;
    private static boolean isCopied = false; // 복사가 되었는지 여부를 저장하는 플래그
    private static Image originalImage = null; // 원래 버튼 이미지

    
   public void draw(Composite parent) {
      GridLayout gridLayout = new GridLayout(1, false);
        parent.setLayout(gridLayout);

        changeComposite = new Composite(parent, SWT.NONE); // changeComposite 생성

        changeComposite.setLayout(new GridLayout());
          
        // Refactor 탭에 추가 텍스트 입력 필드 생성

        Label codeLabel = new Label(changeComposite, SWT.NONE);
        codeLabel.setText("Please enter the code:");

        codeText = new Text(changeComposite, SWT.MULTI | SWT.BORDER | SWT.WRAP);
        GridData codeData = new GridData(SWT.FILL, SWT.CENTER, true, false);
        codeData.widthHint = 500; // 텍스트 입력공간의 좌우 길이 조정
        codeData.heightHint = 300;
        codeText.setLayoutData(codeData);
        
        Label beforeLabel = new Label(changeComposite, SWT.NONE);
        beforeLabel.setText("Example of the original variable name:");

        beforeText = new Text(changeComposite, SWT.MULTI | SWT.BORDER | SWT.WRAP);
        GridData beforeData = new GridData(SWT.FILL, SWT.CENTER, true, false);
        beforeData.widthHint = 150; // 텍스트 입력공간의 좌우 길이 조정
        beforeData.heightHint = 20;
        beforeText.setLayoutData(beforeData);
        
        Label afterLabel = new Label(changeComposite, SWT.NONE);
        afterLabel.setText("Example of the desired variable name:"); // Label의 텍스트를 수정

        afterText = new Text(changeComposite, SWT.MULTI | SWT.BORDER | SWT.WRAP); // afterText 생성
        GridData afterData = new GridData(SWT.FILL, SWT.CENTER, true, false);
        afterData.widthHint = 150; // 텍스트 입력공간의 좌우 길이 조정
        afterData.heightHint = 20;
        afterText.setLayoutData(afterData);
        
        Label purposeLabel = new Label(changeComposite, SWT.NONE);
        purposeLabel.setText("How can I assist you further?:");

        purposeText = new Text(changeComposite, SWT.MULTI | SWT.BORDER | SWT.WRAP);
        GridData purposeData = new GridData(SWT.FILL, SWT.CENTER, true, false);
        purposeData.widthHint = 500; // 텍스트 입력공간의 좌우 길이 조정
        purposeData.heightHint = 100;
        purposeText.setLayoutData(purposeData);
        
        changeButton = new Button(changeComposite, SWT.PUSH);
        changeButton.setText("RENAME");
        GridData buttonData = new GridData(SWT.BOTTOM, SWT.CENTER, false, false);
        changeButton.setLayoutData(buttonData);       
        
        changeButton.addListener(SWT.Selection, e -> {
             
            OpenAIHandler handler = new OpenAIHandler();
            
//            String selectedText = purposeText.getText() + "\n Translate this text into English";
//            
//            try {
//                response = (String) handler.callOpenAPI(selectedText); 
//            } catch (ExecutionException ex) {
//                ex.printStackTrace();
//            }
            
//            selectedText = "Please rename only the variables as in the example of changing the name of variable " + beforeText.getText() + " to "
//                 + afterText.getText() + " in the following code. Additionally, add renaming options like that" + response + "\n" + codeText.getText(); // Submit 버튼 클릭 시 Text 위젯의 값을 가져옵니다. 

//            selectedText = "{\r\n"
//                  + "  \"message\": \"Hello! I need assistance with modifying variable names in my code.\",\r\n"
//                  + "  \"code\": \" " + codeLabel.getText() + " \",\r\n"
//                  + "  \"variable_example\": {\r\n"
//                  + "    \"original_name\": \" " + beforeLabel.getText() + " \",\r\n"
//                  + "    \"desired_name\": \" " + afterLabel.getText() + " \"\r\n"
//                  + "  }\r\n"
//                  + "}";
           String escapedCode = OpenAIHandler.escapeJson(codeText.getText());

            
            String selectedText = "{\"model\": \"gpt-3.5-turbo\", \"messages\": "
                    + "[{\"role\": \"system\", \"content\": \"Apply a simialr modification to 'all' variable names in this code based on a single example.\"},"
                    + "{\"role\": \"user\", \"content\": \"code: " + escapedCode
                    + ", Example of the original variable name: " + beforeText.getText()
                    + ", Example of the desired variable name: " + afterText.getText()
                    + ", Additional options to consider: " + purposeText.getText()
                    + "\"}], \"max_tokens\": 500}";
            
            try {
                response = (String) handler.callOpenAPI(selectedText); 
            } catch (ExecutionException ex) {
                ex.printStackTrace();
            }
            
            openNewWindow2();
          });
        
        clearButton = new Button(changeComposite, SWT.PUSH); // 추가된 부분
        clearButton.setText("CLEAR"); // 추가된 부분
        GridData buttonData5 = new GridData(SWT.TOP, SWT.TOP, false, false); // 추가된 부분
        clearButton.setLayoutData(buttonData5); // 추가된 부분
        clearButton.addListener(SWT.Selection, e -> { // 추가된 부분
            clearInputFields(); // 추가된 부분
        }); // 추가된 부분
        

   }
   
   private void openNewWindow2() {
        Shell newShell2 = new Shell(Display.getDefault());
        newShell2.setText("RENAME");
        newShell2.setLayout(new GridLayout(1, false));
        
        InputStream is = getClass().getResourceAsStream("/icons/clipboard.png");
        Image copyImage = null;
        if (is != null) {
            copyImage = new Image(Display.getDefault(), is);
            copyImage = resize(copyImage, 24, 24);  // 24x24 크기로 리사이징
        } else {
            System.out.println("Image not found");
        }
        
    
        InputStream isDone = getClass().getResourceAsStream("/icons/check.png"); // 'done' 이미지 리소스를 로드, 경로는 예시입니다.
        if (isDone != null) {
            doneImage = new Image(Display.getDefault(), isDone);
            doneImage = resize(doneImage, 24, 24);
        } else {
            System.out.println("Done Image not found");
        }
        
        
        // 이미지를 버튼에 적용
        copyButton = new Button(newShell2, SWT.PUSH);
        if (copyImage != null) {
            copyButton.setImage(copyImage);
        }
        GridData buttonData4 = new GridData(SWT.END, SWT.BEGINNING, false, false);
        copyButton.setLayoutData(buttonData4);
        
        originalImage = copyButton.getImage(); // 버튼의 원래 이미지 저장
        
      
        
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
     
     
        copyButton.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {
                Clipboard clipboard = new Clipboard(Display.getDefault());
                TextTransfer textTransfer = TextTransfer.getInstance();
                
                if (!isCopied) {
                    try {
                        String textToCopy = infoLabel.getText();
                        clipboard.setContents(new Object[] { textToCopy }, new Transfer[] { textTransfer });
                        
                        // 이미지를 doneImage로 변경
                        if (doneImage != null) {
                            copyButton.setImage(doneImage);
                        }
                        isCopied = true;
                        
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    // 클립보드 내용을 지우고 이미지를 원래대로 복구
                     clipboard.clearContents();

                    if (originalImage != null) {
                        copyButton.setImage(originalImage);
                    }
                    isCopied = false;
                }
                copyButton.redraw();  
                clipboard.dispose();
            }
        });     
        
       
        

        newShell2.setSize(500, 700);
        newShell2.open();
   }
   
     public Image resize(Image image, int width, int height) {
           Image scaled = new Image(Display.getDefault(), width, height);
           GC gc = new GC(scaled);
           gc.setAntialias(SWT.ON);
           gc.setInterpolation(SWT.HIGH);
           gc.drawImage(image, 0, 0, 
                   image.getBounds().width, image.getBounds().height, 
                   0, 0, width, height);
           gc.dispose();
           image.dispose(); // If you don't need the original image, dispose it
           return scaled;
       }
   
   private void clearInputFields() {
        codeText.setText("");
        beforeText.setText("");
        afterText.setText("");
        purposeText.setText("");
    }

}