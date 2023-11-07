package gptpro;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchListener;

public class MyWorkbenchListener implements IWorkbenchListener {
    @Override
    public boolean preShutdown(IWorkbench workbench, boolean forced) {
        // 플러그인이 종료될 때 Job을 취소하고 완료합니다.
        Job[] runningJobs = Job.getJobManager().find(null);
        for (Job job : runningJobs) {
            job.cancel();
        }
        
     // H2 DB 서버를 종료합니다.
        H2ServerStarter.stopServer(); // H2 서버 종료 메서드 호출
        System.out.println("h2 server stop");

        return true;
    }

    @Override
    public void postShutdown(IWorkbench workbench) {
    }
}

