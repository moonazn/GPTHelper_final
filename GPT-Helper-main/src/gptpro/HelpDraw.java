package gptpro;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import java.io.IOException;
import java.io.InputStream;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.layout.GridLayout;




public class HelpDraw {
   private Button EnglishButton;
   private Button KoreanButton;
    private Image EnglishImage;
    private Image KoreanImage;
    public void draw(Composite parent) {
       
       Display display = Display.getDefault();

       // 이미지 리소스 로드
       InputStream englishImageStream = getClass().getResourceAsStream("/icons/english.png");
       InputStream koreanImageStream = getClass().getResourceAsStream("/icons/korean.png");

       if (englishImageStream != null && koreanImageStream != null) {
           EnglishImage = new Image(display, englishImageStream);
           KoreanImage = new Image(display, koreanImageStream);

           // 이미지 리소스 스트림 닫기
           try {
               englishImageStream.close();
               koreanImageStream.close();
           } catch (IOException e) {
               e.printStackTrace();
           }

           // 이미지 리사이즈
           EnglishImage = resize(EnglishImage, 24, 24);
           KoreanImage = resize(KoreanImage, 24, 24);

           // 별도의 컴포지트 생성
           Composite buttonComposite = new Composite(parent, SWT.NONE);
           buttonComposite.setLayout(new GridLayout(2, false));

           // 영어 버튼 생성 및 이미지 설정
           EnglishButton = new Button(buttonComposite, SWT.PUSH);
           EnglishButton.setImage(EnglishImage);

           // 한국어 버튼 생성 및 이미지 설정
           KoreanButton = new Button(buttonComposite, SWT.PUSH);
           KoreanButton.setImage(KoreanImage);

           // 그리드 데이터 설정
           GridData buttonCompositeGridData = new GridData(SWT.BEGINNING, SWT.CENTER, false, false);
           buttonComposite.setLayoutData(buttonCompositeGridData);

       } else {
           System.out.println("Image not found");
       }


       

        GridData midTextGridData = new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1);
        midTextGridData.widthHint = 500;
        midTextGridData.heightHint = 118;
        
        GridData smallTextGridData = new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1);
        smallTextGridData.widthHint = 500;
        smallTextGridData.heightHint = 42;

        Font boldFont = new Font(display, parent.getFont().getFontData()[0].getName(), parent.getFont().getFontData()[0].getHeight(), SWT.BOLD);
        
     // RequireTab
        Label requireLabel = new Label(parent, SWT.NONE);
        requireLabel.setText("[RequireTab]");
        requireLabel.setFont(boldFont);
        Text text0 = new Text(parent, SWT.BORDER | SWT.MULTI | SWT.WRAP | SWT.V_SCROLL| SWT.READ_ONLY);
        text0.setLayoutData(midTextGridData);
        text0.setText("1. Enter the desired topic for creating a Requirements Specification (SRS) document in the SRS field, and click the SUBMIT button to automatically generate the requirements specification document.\n"
              + "\n"
              + "2. Edit the generated SRS as needed and save it using the SAVE button.\n"
              + "\n"
              + "3. Click the EXCEL button to export the requirements specification document to an Excel file for storage.\n"
              + "\n"
              + "4. If you want the code to be implemented in its entirety, click the 'full coding' button below. "
              + "If you want a partial option, click the save button after editing the text and double-click the ID of the part you want.\n"
              + "\n"
              + "5. The automatically generated code can be easily copied to the clipboard using the COPY button (clipboard icon) at the bottom and pasted as needed." );

        
        // CreateTab
        Label createLabel = new Label(parent, SWT.NONE);
        createLabel.setText("[CreateTab]");
        createLabel.setFont(boldFont);
        Text text1 = new Text(parent, SWT.BORDER | SWT.MULTI | SWT.WRAP | SWT.V_SCROLL| SWT.READ_ONLY);
        text1.setLayoutData(midTextGridData);
        text1.setText("1. Enter the method name of the code you want to create in the\n 'Method's name' field.\r\n"
              + "\n"
                 + "2. Enter the purpose for generating the code in the 'Purpose'\n field.\r\n"
                 + "\n"
                + "3. Enter the variable names required for the code method in the\n 'Variable's Name' field. (can be omitted)\r\n" );

        // ExplainTab
        Label explainLabel = new Label(parent, SWT.NONE);
        explainLabel.setText("[ExplainTab]");
        explainLabel.setFont(boldFont);
        Text text2 = new Text(parent, SWT.BORDER | SWT.MULTI | SWT.WRAP | SWT.V_SCROLL| SWT.READ_ONLY);
        text2.setLayoutData(smallTextGridData);
        text2.setText("1. Enter the code generated by gpt in the 'Please enter the code'\n field.\r\n");

        // FindTab
        Label findLabel = new Label(parent, SWT.NONE);
        findLabel.setText("[FindTab]");
        findLabel.setFont(boldFont);
        Text text3 = new Text(parent, SWT.BORDER | SWT.MULTI | SWT.WRAP | SWT.V_SCROLL| SWT.READ_ONLY);
        text3.setLayoutData(smallTextGridData);
        text3.setText("1. Enter the desired API name in the 'Enter OpenAPI content'\n field.\r\n");

        // RenameTab
        Label renameLabel = new Label(parent, SWT.NONE);
        renameLabel.setText("[RenameTab]");
        renameLabel.setFont(boldFont);
        Text text4 = new Text(parent, SWT.BORDER | SWT.MULTI | SWT.WRAP | SWT.V_SCROLL| SWT.READ_ONLY);
        text4.setLayoutData(midTextGridData);
        text4.setText("1. Enter the code generated by gpt in the 'Please enter the code'\n field.\r\n"
              + "\n"
                + "2. Enter the original variable name.\r\n"
                + "\n"
                + "3. Enter the variable name you want to change.\r\n"
                + "\n"
                + "4. If you have any additional information, enter it in the\n 'How can I assist you further?' field.");
        
        parent.addDisposeListener(e -> {
            boldFont.dispose();
        });
        
     // 처음에 있는 영어 버튼을 눌렀을 때의 텍스트 내용을 저장하는 변수들
        String originalText0 = text0.getText();
        String originalText1 = text1.getText();
        String originalText2 = text2.getText();
        String originalText3 = text3.getText();
        String originalText4 = text4.getText();
     // 영어 버튼 클릭 이벤트 핸들링
        EnglishButton.addSelectionListener((SelectionListener) new SelectionAdapter() {
             @Override
               public void widgetSelected(SelectionEvent e) {
                   // 처음에 있는 텍스트 내용으로 복원
                   text0.setText(originalText0);
                   text1.setText(originalText1);
                   text2.setText(originalText2);
                   text3.setText(originalText3);
                   text4.setText(originalText4);
               }
        });
        
     // Korean Button 클릭 이벤트 핸들링
        KoreanButton.addSelectionListener((SelectionListener) new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                // 한국어로 변경된 내용을 텍스트 위젯에 설정
                text0.setText("1. 요구 사항 명세서 (SRS) 문서를 생성하기 위한 원하는 주제를 SRS 필드에 입력하고 SUBMIT 버튼을 클릭하여 요구 사항 명세서 문서를 자동으로 생성합니다.\n"
                    + "\n"
                    + "2. 생성된 SRS를 필요에 따라 편집하고 SAVE 버튼을 사용하여 저장합니다.\n"
                    + "\n"
                    + "3. 요구 사항 명세서 문서를 저장하기 위해 EXCEL 버튼을 클릭하여 Excel 파일로 내보낼 수 있습니다.\n"
                    + "\n"
                    + "4. 코드를 전체적으로 구현하려면 아래쪽의 '전체 코드' 버튼을 클릭하세요. 일부 옵션을 원하는 경우 텍스트를 편집한 후 원하는 부분의 ID를 두 번 클릭하여 저장 버튼을 클릭하세요.\n"
                    + "\n"
                    + "5. 자동으로 생성된 코드를 클립보드에 복사하려면 하단의 'COPY' 버튼 (클립보드 아이콘)을 사용하여 필요한 곳에 붙여넣을 수 있습니다.");
                
                text1.setText("1. 'Method's name' 필드에 작성할 코드의 Method name을 입력합니다.\n"
                      + "\n"
                        + "2. 'Purpose' 필드에 코드 생성 목적을 입력합니다.\n"
                        + "\n"
                       + "3. 코드 메서드에 필요한 변수 이름을 'Variable's Name' 필드에 입력합니다. (생략 가능)\n" );
                
                text2.setText("1. gpt에서 생성된 코드를 'Please enter the code' 필드에 입력합니다.\n");
                
                
                text3.setText("1. 'Enter OpenAPI content' 필드에 원하는 API 이름을 입력합니다.\n");
                
                text4.setText("1. Please enter the code' 필드에 GPT에서 생성한 코드를 입력합니다.\n"
                      + "\n"
                        + "2. 원본 변수 이름을 입력합니다.\n"
                        + "\n"
                        + "3. 변경하려는 변수 이름을 입력합니다.\n"
                        + "\n"
                        + "4. 추가 정보가 있으면  'How can I assist you further?' 필드에 입력합니다.");
            }
            
        });
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