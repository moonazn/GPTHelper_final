package gptpro;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

public class DatabaseConnectionDialog extends Dialog {

    private Button autoStartButton; // 자동으로 시작할지 여부를 선택하는 버튼
    
    private static boolean autoStart = false; // 자동으로 시작할지 여부를 저장하는 변수

    public DatabaseConnectionDialog(Shell parentShell) {
        super(parentShell);
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite container = (Composite) super.createDialogArea(parent);
        GridLayout layout = new GridLayout(2, false);
        container.setLayout(layout);

        Label titleLabel = new Label(container, SWT.NONE);
        titleLabel.setText("< DATABASE 생성 >");
        GridData gridData = new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 2);
        titleLabel.setLayoutData(gridData);
        
        // Host
        Label hostLabel = new Label(container, SWT.NONE);
        hostLabel.setText("Host:  localhost");
     // hostLabel에 대한 레이아웃 데이터 설정
        GridData hostLabelData = new GridData(SWT.FILL, SWT.CENTER, true, false);
        hostLabelData.horizontalSpan = 2; // 가로로 2칸 차지
        hostLabel.setLayoutData(hostLabelData);
        
     // 자동 시작 옵션 추가
        autoStartButton = new Button(container, SWT.CHECK);
        autoStartButton.setText("자동으로 H2 서버 시작");

        // GridData를 사용하여 가로로 2줄 차지하도록 설정
        gridData = new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1);
        autoStartButton.setLayoutData(gridData);

        autoStartButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                autoStart = autoStartButton.getSelection();
            }
        });
        
        // Username
        Label usernameLabel = new Label(container, SWT.NONE);
        usernameLabel.setText("Username:  user");
        usernameLabel.setLayoutData(gridData);
        
        // Password
        Label passwordLabel = new Label(container, SWT.NONE);
        passwordLabel.setText("Password:  1234");
        passwordLabel.setLayoutData(gridData);

        return container;
    }

    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
    }

    @Override
    protected void buttonPressed(int buttonId) {
        if (buttonId == IDialogConstants.OK_ID) {
            // Retrieve user input

        }
        super.buttonPressed(buttonId);
    }
    
    public static boolean shouldAutoStartServer() {
        return autoStart;
    }
}