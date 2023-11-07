package gptpro;


import java.io.InputStream;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.graphics.Image;

import org.eclipse.swt.graphics.GC;

import org.eclipse.swt.dnd.Clipboard;



public class CreateDraw {
   
   public static Label ansLabel;
   
    private Text varText;
    private Text methodText; 
    private Text purposeText;
    private Button submitButton;
    private Button copyButton;
    private Button clearButton;
    private Image doneImage;
    public static Text responseText;
    private static boolean isCopied = false; // 복사가 되었는지 여부를 저장하는 플래그
    private static Image originalImage = null; // 원래 버튼 이미지
   
   public void create(Composite parent) {
      
      
      // Create 탭에 추가 텍스트 입력 필드 생성

        Label methodLabel = new Label(parent, SWT.NONE);
        methodLabel.setText("Method's name: " + "   ex) sum");
   

        methodText = new Text(parent, SWT.SINGLE | SWT.BORDER);
        GridData methodData = new GridData(SWT.FILL, SWT.CENTER, true, false);
        methodData.widthHint = 500; // 텍스트 입력공간의 좌우 길이 조정
        methodData.heightHint = 20;
        methodText.setLayoutData(methodData);
        
        Label purposeLabel = new Label(parent, SWT.NONE);
        purposeLabel.setText("Purpose:  " + "           " + " ex) Add num1 and num2, return the total");

        purposeText = new Text(parent, SWT.MULTI | SWT.BORDER | SWT.WRAP);
        GridData purposeData = new GridData(SWT.FILL, SWT.CENTER, true, false);
        purposeData.widthHint = 500; // 텍스트 입력공간의 좌우 길이 조정
        purposeData.heightHint = 50;
        purposeText.setLayoutData(purposeData);

        Label varLabel = new Label(parent, SWT.NONE);
        varLabel.setText("Variables' Name: "+"  ex) num1, num2, total");

        varText = new Text(parent, SWT.SINGLE | SWT.BORDER);
        GridData varData = new GridData(SWT.FILL, SWT.CENTER, true, false);
        varData.widthHint = 500; // 텍스트 입력공간의 좌우 길이 조정
        varData.heightHint = 20;
        varText.setLayoutData(varData);

        submitButton = new Button(parent, SWT.PUSH);
        submitButton.setText("SUBMIT");
        GridData buttonData3 = new GridData(SWT.BEGINNING, SWT.BEGINNING, false, false);
        submitButton.setLayoutData(buttonData3);       
        
        responseText = new Text(parent, SWT.WRAP);
        responseText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        // 다른 곳에서 받아온 정보를 출력하기 위해 해당 정보를 변수에 저장한 뒤 setText() 메서드로 Label에 설정
        
     
        
  
        
        submitButton.addListener(SWT.Selection, e -> {
           
          String escapedCode = OpenAIHandler.escapeJson(purposeText.getText());
           
          String selectedText = "{\"model\": \"gpt-3.5-turbo\", \"messages\": "
                + "[{\"role\": \"system\", \"content\": \"Create a java method code and give a code only.\"},"
                + "{\"role\": \"user\", \"content\": \"method's name: " + methodText.getText() 
                + ", variables' names: " + varText.getText()
                + ", Description of the method's purpose and functionality: " + escapedCode
                + "\"}], \"max_tokens\": 500}";
          
          OpenAIHandler handler = new OpenAIHandler();

          try {
               String response = (String) handler.callOpenAPI(selectedText); 
               if (response != null) {
                   Display.getDefault().asyncExec(() -> {
                       if (responseText != null && !responseText.isDisposed()) {
                           responseText.setText(response);
                       }
                   });
               }
           } catch (ExecutionException ex) {
              ex.printStackTrace();
               Throwable cause = ex.getCause();
               if (cause != null) {
                   cause.printStackTrace();
               }
           }
        });
        
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
        copyButton = new Button(parent, SWT.PUSH);
        if (copyImage != null) {
            copyButton.setImage(copyImage);
        }
        GridData buttonData4 =   new GridData(SWT.BEGINNING, SWT.BEGINNING, false, false);
        copyButton.setLayoutData(buttonData4);
        
        originalImage = copyButton.getImage(); // 버튼의 원래 이미지 저장
        Clipboard clipboard = new Clipboard(Display.getCurrent());
        
        copyButton.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {
                try {
                    if(!isCopied) {
                        new Copy().execute(new ExecutionEvent());
                        if (doneImage != null) {
                            copyButton.setImage(doneImage);
                        }
                        isCopied = true;
                    } else {
                        // 클립보드 내용을 지우고 원래 이미지로 복귀
                        
                        clipboard.clearContents();
                        
                        if (originalImage != null) {
                            copyButton.setImage(originalImage);
                        }
                        isCopied = false;
                    }
                    copyButton.redraw();  // 또는 copyButton.update();
                } catch (ExecutionException ex) {
                    ex.printStackTrace();
                    Throwable cause = ex.getCause();
                    if (cause != null) {
                        cause.printStackTrace();
                    }
                }
            }
        });
        

        clearButton = new Button(parent, SWT.PUSH);
        clearButton.setText("CLEAR");
        GridData buttonData5 = new GridData(SWT.BEGINNING, SWT.BEGINNING, false, false);
        clearButton.setLayoutData(buttonData5);
        clearButton.addListener(SWT.Selection, e -> {
            clearInputFields();
        });
   }
   
    private void clearInputFields() {
           methodText.setText("");
           purposeText.setText("");
           varText.setText("");
           responseText.setText("");
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
}