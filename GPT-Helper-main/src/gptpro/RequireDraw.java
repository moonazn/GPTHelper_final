package gptpro;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
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
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.custom.StyleRange;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileOutputStream;
import java.io.File;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.io.BufferedWriter;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;



public class RequireDraw {

   
    private String response1;
    private String response2;
    private Button submitButton1;
    private Button submitButton2;
    private Button editingEndButton;
    private Button ChangeExcelButton;
    public static Text requireText;
    public static Text codeText;
    private Label filePathLabel;
    private Button copyButton;
    private Button clearButton;
   
    public static StyledText responseText;
    private Image doneImage;
    public static String clickedText;
    private static boolean isCopied = false; // 복사가 되었는지 여부를 저장하는 플래그
    private static Image originalImage = null; // 원래 버튼 이미지

   
    public void draw(Composite parent) {
       Display display = parent.getDisplay();
        parent.setLayout(new GridLayout(2, false)); // 두 개의 열로 배치

        Label codeLabel = new Label(parent, SWT.NONE);
        codeLabel.setText("desired topic of SRS (Software Requirements Specification) :");

        requireText = new Text(parent, SWT.MULTI | SWT.BORDER | SWT.WRAP);
        GridData requireData = new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1); // 두 열에 걸쳐 표시
        requireData.widthHint = 100; // 텍스트 입력공간의 좌우 길이 조정
        requireData.heightHint = 25;
        requireText.setLayoutData(requireData);

        submitButton1 = new Button(parent, SWT.PUSH);
        submitButton1.setText("SUBMIT");
        GridData buttonData1 = new GridData(SWT.BOTTOM, SWT.CENTER, false, false, 2, 1); // 두 열에 걸쳐 표시
        buttonData1.horizontalSpan = 2; // 두 열에 걸쳐 표시
        submitButton1.setLayoutData(buttonData1);
        
        
//        infoButton = new Button(parent, SWT.PUSH);
//        GridData infoButtonData1 = new GridData(SWT.BOTTOM, SWT.CENTER, false, false, 1, 1); 
//        infoButton.setLayoutData(infoButtonData1);
//        Image infoImage = display.getSystemImage(SWT.ICON_INFORMATION);
//        infoButton.setImage(infoImage);
//        
//        infoButton.setToolTipText("Hover over me to see the tooltip!");

        
        // responseText 생성 및 레이아웃 설정
        responseText = new StyledText(parent, SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
        GridData textData = new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1);
        textData.widthHint = 300; // 텍스트 출력공간의 좌우 길이 조정
        textData.heightHint = 400;
        responseText.setLayoutData(textData);
        responseText.setEditable(true);

        // CodeText 생성 및 레이아웃 설정
        codeText = new Text(parent, SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
        GridData codeTextData = new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1);
        codeTextData.widthHint = 300;
        codeTextData.heightHint = 400;
        codeText.setLayoutData(codeTextData);
        codeText.setEditable(true);
        
//      infoImage.dispose();
        
      InputStream excel = getClass().getResourceAsStream("/icons/excel.png");
      Image excelImage = null;
      if (excel != null) {
         excelImage = new Image(Display.getDefault(), excel);
         excelImage = resize(excelImage, 24, 24);  // 24x24 크기로 리사이징
      } else {
          System.out.println("Image not found");
      }

      // 이미지를 버튼에 적용
      ChangeExcelButton = new Button(parent, SWT.PUSH);
      if (excelImage != null) {
         ChangeExcelButton.setImage(excelImage);
      }
      GridData excelData =  new GridData(SWT.BEGINNING, SWT.CENTER, false, false); // 가로로 나열
      ChangeExcelButton.setLayoutData(excelData);
      
     

      ChangeExcelButton.addListener(SWT.Selection, e -> {
         
          String requireTextContent = requireText.getText();
             String fileName = requireTextContent.replaceAll("\\s+", "_").toLowerCase() + ".csv";
             String os = System.getProperty("os.name").toLowerCase();

          // 운영체제 확인 후 바탕화면 경로 설정
          String desktopPath;
          if (os.contains("win")) { // 윈도우일 경우
              desktopPath = System.getProperty("user.home") + "\\Desktop\\";
          } else if (os.contains("mac") || os.contains("nix") || os.contains("nux") || os.contains("aix")) { // macOS, Linux 등 Unix-like OS
              desktopPath = System.getProperty("user.home") + "/Desktop/";
          } else {
              // 다른 운영체제 (또는 판단할 수 없는 경우)
              desktopPath = System.getProperty("user.home") + File.separator;
          }

          File file = new File(desktopPath + fileName);       

          int index = 0;
       String baseFileName = requireTextContent.replaceAll("\\s+", "_").toLowerCase();
       
       // 파일 이름 확인 및 새 파일 이름 생성
       while (file.exists()) {
           System.out.println("File already exists");
           
           index++;
           String newFileName = baseFileName + "_최신" + (index == 1 ? "" : "_" + index) + ".csv";
           file = new File(desktopPath + newFileName);
       }
       
       filePathLabel.setText("File saved at: " + file.getAbsolutePath());

       try (FileOutputStream fos = new FileOutputStream(file);
              OutputStreamWriter osw = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
              BufferedWriter writer = new BufferedWriter(osw)) {

             // UTF-8 BOM 및 CSV 헤더 추가
             writer.write('\ufeff');
             writer.append("요구사항 ID,요구사항명,세부사항\n");

             // 정규식 패턴을 정의
             Pattern pattern = Pattern.compile("요구사항 ID:\\s*(.+?)\\s*\\n요구사항명:\\s*(.+?)\\s*\\n세부사항:\\s*\\n(.+?)(?=\\s*\\n요구사항 ID:|$)", Pattern.DOTALL);

             // Matcher 객체를 생성
             Matcher matcher = pattern.matcher(responseText.getText());

             // 이전 요구사항 ID와 요구사항명을 저장할 변수를 초기화
             String lastRequirementId = "";
             String lastRequirementName = "";

             // 매치된 요구사항을 찾아서 리스트에 저장하고 CSV 파일에 작성
             while (matcher.find()) {
                 String id = matcher.group(1).trim();
                 String name = matcher.group(2).trim();
                 String details = matcher.group(3).trim();
                 
                 // 요구사항명이 바뀌면 한 줄 띄우기
                 if (!name.equals(lastRequirementName)) {
                     writer.append("\n");
                 }


                 // ID와 이름을 정리
                 String cleanId = id.equals(lastRequirementId) ? "" : id.replace("\"", "\"\"").replaceAll("[,]", " ");
                 String cleanName = name.equals(lastRequirementName) ? "" : name.replace("\"", "\"\"").replaceAll("[,]", " ");

                 // 이전 요구사항 업데이트
                 lastRequirementId = id;
                 lastRequirementName = name;

                 String[] detailItems = details.split("\n");
                 for (String item : detailItems) {
                     String escapedItem = item.replace("\"", "\"\"");
                     String cleanItem = escapedItem.replaceAll("[,]", " ").replace("-", " -");

                     writer.append("\"").append(cleanId).append("\",\"")
                         .append(cleanName).append("\",\"")
                         .append(cleanItem).append("\"\n");

                     // 다음 세부사항에 대해 ID와 이름을 출력하지 않도록 빈 문자열로 설정
                     cleanId = "";
                     cleanName = "";
                 }
             }
             System.out.println("CSV file created");
         } catch (IOException ex) {
             ex.printStackTrace();
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
        
        // 먼저 두 번째 이미지도 불러옵니다. 예를 들어, "icons/clipboard_done.png"라는 경로에 있다고 가정하겠습니다.
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
        GridData buttonData3 =  new GridData(SWT.BEGINNING, SWT.CENTER, false, false); // 가로로 나열
        copyButton.setLayoutData(buttonData3);

        originalImage = copyButton.getImage(); // 버튼의 원래 이미지 저장
       
        
      
        
        Clipboard clipboard = new Clipboard(Display.getCurrent());
        
        
        
        copyButton.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {
                if (!isCopied) {
                    // 클립보드에 텍스트 복사
                    String textData = codeText.getText();
                    
                    String extractedText = Copy.extractTextBetweenJava(textData);

                    TextTransfer textTransfer = TextTransfer.getInstance();
                    clipboard.setContents(new Object[] { extractedText }, new Transfer[] { textTransfer });

                    // 복사가 완료되면 이미지 변경
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
            }
        });
        
        
        
        // save button
        editingEndButton = new Button(parent, SWT.PUSH);
        editingEndButton.setText("SAVE");
        GridData editingEndButData =  new GridData(SWT.FILL, SWT.END, false, false);
        editingEndButton.setLayoutData(editingEndButData);
     
        
        
        Label emptyLabel = new Label(parent, SWT.NONE);
        emptyLabel.setText("");
        GridData emptyLabelData = new GridData(SWT.FILL, SWT.FILL, false, false);
        emptyLabel.setLayoutData(emptyLabelData);


        submitButton1.addListener(SWT.Selection, e -> {
           String escapedCode1 = OpenAIHandler.escapeJson(requireText.getText());
           String selectedText1 = "{\"model\": \"gpt-3.5-turbo\", \"messages\": ["
                   + "{\"role\": \"system\", \"content\": \"요구사항ID가 있는 요구사항 정의서를 세부적으로 작성해줘\"},"
                   + "{\"role\": \"user\", \"content\": \"code: " + escapedCode1 + "\"},"
                   + "{\"role\": \"assistant\", \"content\": \"형식은 다음과 같이 기능들을 나열해. 요구사항 ID :  \\n 요구사항명 :  \\n 세부사항 : -  \"}"
                   + "], \"max_tokens\": 3000}";

           OpenAIHandler handler = new OpenAIHandler();

            try {
                response1 = (String) handler.callOpenAPI(selectedText1);
                responseText.setText(response1); // response를 responseText에 표시
                
            } catch (ExecutionException ex) {
                ex.printStackTrace();
            }
        });
        
        editingEndButton.addListener(SWT.Selection, e -> {
           
           // 기존 스타일 범위 삭제
            responseText.setStyleRanges(new StyleRange[0]);
           
            Pattern pattern = Pattern.compile("요구사항 ID(.*?)(?=\\s*요구사항명)", Pattern.DOTALL);
            Matcher matcher = pattern.matcher(responseText.getText());

            responseText.setLayoutData(textData);

            ArrayList<String> extractedTexts = new ArrayList<>();
            while (matcher.find()) {
               String str = matcher.group(1).trim();
               if (str.startsWith(":")) {
                    str = str.substring(1).trim();
                }
                extractedTexts.add(str);
            }
            
            System.out.println(extractedTexts);
         // Define the link style
            StyleRange[] styleRanges = new StyleRange[extractedTexts.size()];
            int startIndex = 0; // Track the current position in the response text
            
            for (int i = 0; i < extractedTexts.size(); i++) {
                String extractedText = extractedTexts.get(i);
                startIndex = responseText.getText().indexOf(extractedText, startIndex); // Find the starting index
                int length = extractedText.length();

                StyleRange linkStyle = new StyleRange();
                linkStyle.start = startIndex;
                linkStyle.length = length;
                linkStyle.underline = true;
                linkStyle.foreground = display.getSystemColor(SWT.COLOR_BLUE);

                styleRanges[i] = linkStyle;

                // Move the startIndex to the next position to avoid overlapping
                startIndex += length;
            }


            // Apply the link style to the "Click here" text
            responseText.setStyleRanges(styleRanges);


            responseText.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    StyledText clickedStyledText = (StyledText) e.widget;
                    int clickedOffset = clickedStyledText.getSelection().x;

                    for (int i = 0; i < styleRanges.length; i++) {
                        StyleRange styleRange = styleRanges[i];
                        if (clickedOffset >= styleRange.start && clickedOffset < styleRange.start + styleRange.length) {
                            clickedText = extractedTexts.get(i);
                            
                            codeText.setText("Please wait for a second..");
                            System.out.println("Link clicked: " + clickedText);
                            // 클릭한 링크에 대한 동작을 여기에 추가합니다.
                            
                            PartCoding partCode = new PartCoding();
                            partCode.partCoding();
                            
                            break;
                        }
                    }
                }
            });

        });
        
        
        
        submitButton2 = new Button(parent, SWT.PUSH);
        submitButton2.setText("Full Coding >>");
        GridData buttonData2 =  new GridData(SWT.FILL, SWT.BEGINNING, false, false);
        submitButton2.setLayoutData(buttonData2);
        
        submitButton2.addListener(SWT.Selection, e -> {
            String escapedCode2 = OpenAIHandler.escapeJson(responseText.getText());
            String selectedText2 = "{\"model\": \"gpt-3.5-turbo\", \"messages\": "
                    + "[{\"role\": \"system\", \"content\": \"implement it in java code\"},"
                    + "{\"role\": \"user\", \"content\": \"code: " + escapedCode2
                    + "\"}], \"max_tokens\": 3000}";

            OpenAIHandler handler = new OpenAIHandler();

            try {
                response2 = (String) handler.callOpenAPI(selectedText2);
                codeText.setText(response2); // response를 responseText에 표시
            } catch (ExecutionException ex) {
                ex.printStackTrace();
            }
        });


      
        Label emptyLabel1 = new Label(parent, SWT.NONE);
        emptyLabel1.setText("");
        GridData emptyLabelData1 = new GridData(SWT.FILL, SWT.FILL, false, false);
        emptyLabel1.setLayoutData(emptyLabelData1);
        
        
        clearButton = new Button(parent, SWT.PUSH);
        clearButton.setText("CLEAR");
        GridData buttonData4 = new GridData(SWT.FILL, SWT.END, false, false);
        clearButton.setLayoutData(buttonData4);

    
      
        clearButton.addListener(SWT.Selection, e -> {
            clearInputFields();
        });
        
        filePathLabel = new Label(parent, SWT.NONE);
        GridData filePathLabelData = new GridData(SWT.FILL, SWT.FILL, false, false, 2, 1);
        filePathLabel.setLayoutData(filePathLabelData);
        
            
    }
    
    private void clearInputFields() {
       requireText.setText("");
       responseText.setText("");
       codeText.setText("");
        
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