package gptpro;

import org.eclipse.ui.IStartup;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;

public class MyStartupClass implements IStartup {
    @Override
    public void earlyStartup() {
        // MyWorkbenchListener 클래스의 인스턴스를 생성
        MyWorkbenchListener listener = new MyWorkbenchListener();
        
        // IWorkbench.addWorkbenchListener()를 사용하여 listener를 등록
        IWorkbench workbench = PlatformUI.getWorkbench();
        workbench.addWorkbenchListener(listener);

    }
}

