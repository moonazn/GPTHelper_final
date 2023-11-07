package gptpro;
// 얘도 안씀
import org.eclipse.jface.dialogs.PopupDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionAdapter;

public class MemoPopupDialog extends PopupDialog {

    private int line;
    private Text memoTextWidget;
 
    private MemoManager memoManager;

    private boolean shouldCloseOnButtonClick = false;

    public MemoPopupDialog(Shell parent, int line, StyledText editorStyledText) {
        super(parent, SWT.RESIZE | SWT.TITLE | SWT.MODELESS, true, true, false, false, false, null, "Memo");
        this.line = line;
        this.memoManager = new MemoManager(); // 실제 데이터베이스 이름으로 바꾸세요
        editorStyledText.addMouseListener(new EditorMouseListener());

        // 해당 줄에 저장된 메모를 표시
        String memo = memoManager.getMemo(line);
        if (memo != null) {
            memoTextWidget.setText(memo);
        }
    }

    
    @Override
    protected Point getInitialSize() {
        return new Point(400, 250);
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite composite = (Composite) super.createDialogArea(parent);
        composite.setLayout(new GridLayout(1, false));

        // 메모 입력 위젯이 들어갈 Composite
        Composite memoComposite = new Composite(composite, SWT.NONE);
        memoComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        memoComposite.setLayout(new GridLayout(1, false));

        memoTextWidget = new Text(memoComposite, SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
        GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
        gd.heightHint = 200;
        memoTextWidget.setLayoutData(gd);

     // 해당 줄에 저장된 메모를 표시
        String memo = memoManager.getMemo(line);
        if (memo != null) {
            memoTextWidget.setText(memo);
        }


        // 버튼이 들어갈 Composite
        Composite buttonComposite = new Composite(composite, SWT.NONE);
        buttonComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        buttonComposite.setLayout(new GridLayout(2, false));

        // "확인" 버튼 생성
        Button okButton = new Button(buttonComposite, SWT.PUSH);
        okButton.setText("확인");
        okButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        okButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                shouldCloseOnButtonClick = true;
                okPressed();
            }
        });

 

        return composite;
    }

    @Override
    protected void handleShellCloseEvent() {
        if (shouldCloseOnButtonClick) {
            super.handleShellCloseEvent();
        }
    }



    protected void okPressed() {
        String memo = memoTextWidget.getText();
        MemoManager memoManager = new MemoManager(); // 실제 데이터베이스 이름으로 바꾸세요
        memoManager.setMemo(line, memo);
        memoManager.close();

        setReturnCode(OK);

        updateMemoText();
        shouldCloseOnButtonClick = true;
        close();
        
    }

    protected void deletePressed() {
        MemoManager memoManager = new MemoManager(); // 실제 데이터베이스 이름으로 바꾸세요
        memoManager.deleteMemo(line);
        memoManager.close();

        setReturnCode(OK);
        updateMemoText();
        shouldCloseOnButtonClick = true;
        close();
    }


    private void updateMemoText() {
        String memo = memoManager.getMemo(line);
        memoTextWidget.setText(memo != null ? memo : "");
    }

    private class EditorMouseListener extends MouseAdapter {
        @Override
        public void mouseDown(MouseEvent e) {
            
        }
    }
}