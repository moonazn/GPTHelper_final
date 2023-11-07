package gptpro;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

import org.eclipse.ui.texteditor.ITextEditor;

import java.util.HashMap;
import java.util.Map;


public class AddEditMemoAction extends AbstractHandler implements IActionDelegate {

    private Text textArea;
    private int selectedLine;
    private static Map<Integer, MemoDTO> memos; // 각 줄에 대한 메모를 저장하는 Map

    public AddEditMemoAction() {
        memos = new HashMap<>();
    }

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        // This method is called when the action is triggered (e.g., toolbar button clicked).
        // You can implement it to perform any additional functionality.
        // For example, you can open the memo dialog here.
        run(null);
        return null;
    }

    public void run(IAction action) {       
       MemoManager memoManager = new MemoManager();      
       if(!MemoManager.isConnected) {
          return;
       }
       
       ITextEditor editor = getActiveTextEditor();

        if (editor != null) {
            ISelection selection = editor.getSelectionProvider().getSelection();

            if (selection instanceof ITextSelection) {
                selectedLine = ((ITextSelection) selection).getStartLine() + 1;

                // Now you have the current line number in the 'selectedLine' variable
                System.out.println("Current Line Number: " + selectedLine);
            }
        }

        Display display = PlatformUI.getWorkbench().getDisplay();

        // Check if a memo exists for the selected line
        MemoDTO existingMemo = memos.get(selectedLine);
        String existingContent = existingMemo != null ? existingMemo.getContent() : "";
        
        

        // Create or open the memo dialog
        MemoDialog dialog = new MemoDialog(display.getActiveShell(), existingContent);
        if (dialog.open() == Dialog.OK) {
            String memoText = dialog.getMemoText();

            // Update or add the memo for the selected line
            if (!memoText.isEmpty()) {
                MemoDTO memoDTO = new MemoDTO(selectedLine, memoText);
                memos.put(selectedLine, memoDTO);
                memoManager.close();

                // Create or update the icon (marker)
                MarkerManager.createOrUpdateMarker(getActiveTextEditor(), selectedLine, memoText);
            } else {
                // If the memo is empty, remove it from the map and the database
                memos.remove(selectedLine);

                // Delete the memo from the database
                memoManager.deleteMemo(selectedLine);
                memoManager.close();

                // Delete the marker if the memo is empty
                MarkerManager.deleteMarker(getActiveTextEditor(), selectedLine);
                
                textArea.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseDoubleClick(MouseEvent e) {
                        // 더블 클릭하면 MemoDialog를 엽니다.
                        MemoDialog dialog = new MemoDialog(Display.getDefault().getActiveShell(), textArea.getText());
                        if (dialog.open() == Dialog.OK) {
                            String newMemoText = dialog.getMemoText();
                            textArea.setText(newMemoText); // 새 메모로 텍스트 업데이트
                            updateMemo(selectedLine, newMemoText); // 맵과 데이터베이스에서 메모 업데이트
                        }
                    }

               private void updateMemo(int selectedLine, String newMemoText) {
                  // TODO Auto-generated method stub
                  
               }
                });
                
            }
        }
    }

    public ITextEditor getActiveTextEditor() {
        IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();

        if (page != null) {
            IEditorPart editorPart = page.getActiveEditor();

            if (editorPart instanceof ITextEditor) {
                return (ITextEditor) editorPart;
            }
        }

        return null;
    }

    @Override
    public void selectionChanged(IAction action, ISelection selection) {
        // This method is called when the selection in the editor changes.
        // You can implement it if you need to react to selection changes.
    }

    public class MemoDialog extends Dialog {
       
        private String editedMemoText = "";

        public MemoDialog(Shell parentShell, String initialText) {
            super(parentShell);
            editedMemoText = initialText;
        }



        @Override
        protected void configureShell(Shell shell) {
            super.configureShell(shell);
            shell.setText("Memo");
        }

        @Override
        protected void createButtonsForButtonBar(Composite parent) {
            
        }

        @Override
        protected Control createDialogArea(Composite parent) {
            Composite composite = (Composite) super.createDialogArea(parent);
            composite.setLayout(new GridLayout(1, false));
            
            MemoManager memoManager = new MemoManager();
            String memoTextFromDB = memoManager.getMemo(selectedLine);
            memoManager.close();
            

            // Check if a memo exists for the selected line
            MemoDTO existingMemo = memos.get(selectedLine);
            String existingContent = existingMemo != null ? existingMemo.getContent() : "";

            
            textArea = new Text(composite, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
            GridData textData = new GridData(SWT.FILL, SWT.FILL, true, true);
            textData.widthHint = 300;
            textData.heightHint = 200;
            textArea.setLayoutData(textData);
            textArea.setText(existingContent);
            textArea.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseDoubleClick(MouseEvent e) {
                    // 더블 클릭하면 MemoDialog를 엽니다.
                    MemoDialog dialog = new MemoDialog(Display.getDefault().getActiveShell(), textArea.getText());
                    if (dialog.open() == Dialog.OK) {
                        String newMemoText = dialog.getMemoText();
                        textArea.setText(newMemoText); // 새 메모로 텍스트 업데이트
                        updateMemo(selectedLine, newMemoText); // 맵과 데이터베이스에서 메모 업데이트
                    }
                }

                private void updateMemo(int line, String newMemoText) {
                    // 맵에서 메모 업데이트
                    MemoDTO memo = memos.get(line);
                    if (memo != null) {
                        memo.setContent(newMemoText);
                    } else {
                        memo = new MemoDTO(line, newMemoText);
                        memos.put(line, memo);
                    }

                    // 데이터베이스에서 메모 업데이트
                    MemoManager memoManager = new MemoManager();
                    memoManager.setMemo(line, newMemoText);
                    memoManager.close();

                    // (마커가 존재하는 경우) 마커 업데이트
                    MarkerManager.createOrUpdateMarker(getActiveTextEditor(), line, memo.getContent());
                }
            });
            
           
            if (memoTextFromDB != null) {
                textArea.setText(memoTextFromDB); 
            }
           
            Composite buttonComposite = new Composite(composite, SWT.NONE);
            buttonComposite.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));
            buttonComposite.setLayout(new GridLayout(2, true));

            Button saveButton = new Button(buttonComposite, SWT.PUSH);
            saveButton.setText("Save");
            GridData saveButtonData = new GridData(SWT.FILL, SWT.FILL, false, false);
            saveButtonData.verticalIndent = 10;
            saveButton.setLayoutData(saveButtonData);

            saveButton.addListener(SWT.Selection, new Listener() {
                public void handleEvent(Event event) {
                    editedMemoText = textArea.getText();
                    setReturnCode(Dialog.OK);

                    // MemoManager를 생성하고 setMemo 메소드를 호출하여 메모를 데이터베이스에 저장
                    MemoManager memoManager = new MemoManager();
                    memoManager.setMemo(selectedLine, editedMemoText);
                    memoManager.close();
                    
                    close();
                }
            });

            Button deleteButton = new Button(buttonComposite, SWT.PUSH);
            deleteButton.setText("Delete");
            GridData deleteButtonData = new GridData(SWT.FILL, SWT.FILL, false, false);
            deleteButtonData.verticalIndent = 10;
            deleteButton.setLayoutData(deleteButtonData);

            deleteButton.addListener(SWT.Selection, new Listener() {
                public void handleEvent(Event event) {
                    editedMemoText = "";
                    textArea.setText("");
                    setReturnCode(Dialog.OK);
                    
                    // 여기서 마커를 삭제합니다.
                    MarkerManager.deleteMarker(getActiveTextEditor(), selectedLine);
                 // MemoManager를 생성하고 setMemo 메소드를 호출하여 메모를 데이터베이스에 저장
                    MemoManager memoManager = new MemoManager();
                    memoManager.deleteMemo(selectedLine);
                    memoManager.close();
                    close();
                }
            });
                       
            return composite;
        }
        
        
        public String getMemoText() {
            return editedMemoText;
        }
    }
}