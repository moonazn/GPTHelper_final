package gptpro;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.h2.tools.Server;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

public class H2ServerStarter {
    private static Server server;

    public static void startServerInBackground() {
        Job job = new Job("H2 Server Start Job") {
            @Override
            protected IStatus run(IProgressMonitor monitor) {
                startServer();
                return Status.OK_STATUS;
            }
        };
        job.setSystem(true); // 백그라운드 작업으로 설정
        job.schedule();
    }

    public static void startServer() {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(
                "java",
                "-cp",
                "/Users/sjy/Downloads/h2/bin/h2-2.2.222.jar",
                "org.h2.tools.Server",
                "-web",
                "-webPort",
                "8082"
            );

            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();
        	// H2 데이터베이스 설치 디렉토리 경로를 설정합니다.
//            String h2DatabasePath = "/Users/sjy/Downloads/h2/bin/h2-2.2.222.jar"; // 실제 디렉토리 경로로 변경
//
//            // H2 데이터베이스 서버 실행 커맨드를 생성합니다.
//            String[] cmd = {
//                "java",
//                "-cp",
//                h2DatabasePath, // h2.jar 파일의 경로 및 이름을 정확히 지정합니다.
//                "org.h2.tools.Server",
//                "-web",
//                "-webPort",
//                "8082"
//            };
//
//            // 커맨드 실행
//            Process process = Runtime.getRuntime().exec(cmd);
            InputStream inputStream = process.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            int exitCode = process.waitFor();

            if (exitCode == 0) {
                System.out.println("H2 데이터베이스 서버가 시작되었습니다.");
                server = Server.createWebServer("-web", "-webPort", "8082").start();
            } else {
                showServerStartFailureDialog(); // 서버 시작 실패 시 다이얼로그 표시
            }
        } catch (Exception e) {
            showServerStartFailureDialog(); // 서버 시작 실패 시 다이얼로그 표시
            e.printStackTrace();
        }
    }

    public static void stopServer() {
        if (server != null) {
            server.stop(); // H2 서버를 종료합니다.
            System.out.println("서버 종료메서드 실행 ");
        }
    }

    public static void showServerStartFailureDialog() {
        Display.getDefault().syncExec(() -> {
            // 경고 다이얼로그를 띄웁니다.
            MessageDialog.openError(
                PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
                "서버 시작 오류",
                "H2 데이터베이스 서버를 시작하지 못했습니다."
            );
        });
    }
}

