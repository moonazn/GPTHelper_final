package gptpro;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.swt.*;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public class View extends ViewPart {
   public static CTabItem helpTab;
    public static CTabItem requireTab;
    public static CTabFolder tabFolder;
    public static CTabItem createTab;
    public static CTabItem explainTab;
    public static CTabItem findOSTab;
    public static CTabItem refactorTab;
    
    public static Label ansLabel1;
    public static Label ansLabel2;
    public static Object infoLabel;
    private static Canvas graphCanvas;
    private ArrayList<Long> memoryUsageList;
    private ArrayList<Double> memoryUsagePercentList;
    private static final Logger logger = Logger.getLogger(View.class.getName());
    
    private Button startMemoryMeasurementButton;
    private Button stopMemoryMeasurementButton;
    private Runnable memoryMeasurementTask;    // 측정 작업을 관리하기 위한 변수

    public View() {
        // TODO Auto-generated constructor stub
    }

    @Override
    public void createPartControl(Composite parent) {
        GridLayout gridLayout = new GridLayout(1, false);
        parent.setLayout(gridLayout);

        ScrolledComposite scrolledComposite = new ScrolledComposite(parent, SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER);

        GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
        scrolledComposite.setLayoutData(data);
        scrolledComposite.setExpandVertical(true);
        scrolledComposite.setExpandHorizontal(true);

        Composite tabFolderComposite = new Composite(scrolledComposite, SWT.NONE);
        GridLayout tabFolderLayout = new GridLayout(4, true);
        tabFolderComposite.setLayout(tabFolderLayout);

        CTabFolder tabFolder = new CTabFolder(tabFolderComposite, SWT.BORDER);
        GridData tabFolderData = new GridData(SWT.FILL, SWT.FILL, true, true, 4, 1);
        tabFolder.setLayoutData(tabFolderData);

        helpTab = new CTabItem(tabFolder, SWT.NONE);
        helpTab.setText(" Help ");
        Composite helpComposite = new Composite(tabFolder, SWT.NONE);
        helpComposite.setLayout(new GridLayout());
        
        requireTab = new CTabItem(tabFolder, SWT.NONE);
        requireTab.setText(" Require ");
        Composite requireComposite = new Composite(tabFolder, SWT.NONE);
        requireComposite.setLayout(new GridLayout());
        
        createTab = new CTabItem(tabFolder, SWT.NONE);
        createTab.setText(" Create ");
        Composite createComposite = new Composite(tabFolder, SWT.NONE);
        createComposite.setLayout(new GridLayout());

        explainTab = new CTabItem(tabFolder, SWT.NONE);
        explainTab.setText(" Explain ");
        Composite explainComposite = new Composite(tabFolder, SWT.NONE);
        explainComposite.setLayout(new GridLayout());

        findOSTab = new CTabItem(tabFolder, SWT.NONE);
        findOSTab.setText(" Find OpenAPI ");
        Composite findOpenSourceComposite = new Composite(tabFolder, SWT.NONE);
        findOpenSourceComposite.setLayout(new GridLayout());

        refactorTab = new CTabItem(tabFolder, SWT.NONE);
        refactorTab.setText(" Rename ");
        Composite refactorComposite = new Composite(tabFolder, SWT.NONE);
        refactorComposite.setLayout(new GridLayout());

        tabFolder.setSelection(0);

        scrolledComposite.setContent(tabFolderComposite);
        scrolledComposite.setMinSize(tabFolderComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));

        // 버튼 탭 비율 조정
        tabFolder.setSimple(false);
        tabFolder.setLayout(new GridLayout(4, true));
        
        new HelpDraw().draw(helpComposite);
        helpTab.setControl(helpComposite);
        
        helpTab.getParent().addListener(SWT.Resize, event -> {
            int tabWidth = helpTab.getParent().getSize().x / 4;
            GridData helpData = (GridData) helpComposite.getLayoutData();

            if (helpData == null) {
                helpData = new GridData(SWT.FILL, SWT.FILL, true, false);
                helpComposite.setLayoutData(helpData);
            }
            helpData.widthHint = tabWidth;
            helpComposite.setLayoutData(helpData);
            helpComposite.getParent().layout();
        });
        
        requireTab.getParent().addListener(SWT.Resize, event -> {
            int tabWidth = requireTab.getParent().getSize().x / 4;
            GridData requireData = (GridData) requireComposite.getLayoutData();

            if (requireData == null) {
               requireData = new GridData(SWT.FILL, SWT.FILL, true, false);
               requireComposite.setLayoutData(requireData);
            }
            requireData.widthHint = tabWidth;
            requireComposite.setLayoutData(requireData);
            requireComposite.getParent().layout();
        });

        // 생성 탭 비율 조정
        createTab.getParent().addListener(SWT.Resize, event -> {
            int tabWidth = createTab.getParent().getSize().x / 4;
            GridData createData = (GridData) createComposite.getLayoutData();

            if (createData == null) {
                createData = new GridData(SWT.FILL, SWT.FILL, true, false);
                createComposite.setLayoutData(createData);
            }
            createData.widthHint = tabWidth;
            createComposite.setLayoutData(createData);
            createComposite.getParent().layout();
        });

        new CreateDraw().create(createComposite);
        createTab.setControl(createComposite);

        // 분석 탭 비율 조정
        explainTab.getParent().addListener(SWT.Resize, event -> {
            int tabWidth = explainTab.getParent().getSize().x / 4;
            GridData explainData = (GridData) explainComposite.getLayoutData();

            if (explainData == null) {
                explainData = new GridData(SWT.FILL, SWT.FILL, true, false);
                explainComposite.setLayoutData(explainData);
            }
            explainData.widthHint = tabWidth;
            explainComposite.setLayoutData(explainData);
            explainComposite.getParent().layout();
        });

        // 오픈소스 탭 비율 조정
        findOSTab.getParent().addListener(SWT.Resize, event -> {
            int tabWidth = findOSTab.getParent().getSize().x / 4;
            GridData findOSData = (GridData) findOpenSourceComposite.getLayoutData();

            if (findOSData == null) {
                findOSData = new GridData(SWT.FILL, SWT.FILL, true, false);
                findOpenSourceComposite.setLayoutData(findOSData);
            }
            findOSData.widthHint = tabWidth;
            findOpenSourceComposite.setLayoutData(findOSData);
            findOpenSourceComposite.getParent().layout();
        });

        // 리팩터 탭 비율 조정
        refactorTab.getParent().addListener(SWT.Resize, event -> {
            int tabWidth = refactorTab.getParent().getSize().x / 4;
            GridData refactorData = (GridData) refactorComposite.getLayoutData();

            if (refactorData == null) {
                refactorData = new GridData(SWT.FILL, SWT.FILL, true, false);
                refactorComposite.setLayoutData(refactorData);
            }
            refactorData.widthHint = tabWidth;
            refactorComposite.setLayoutData(refactorData);
            refactorComposite.getParent().layout();
        });

        // 탭에 대한 레이아웃 객체를 미리 생성
        HelpDraw helpLayout = new HelpDraw();
        RequireDraw requireLayout = new RequireDraw();
        CreateDraw createLayout = new CreateDraw();
        ExplainDraw explainLayout = new ExplainDraw();
        FindOpenAPIDraw findOSLayout = new FindOpenAPIDraw();
        RefactorDraw refactorLayout = new RefactorDraw();

        tabFolder.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent event) {
                CTabItem selectedTab = (CTabItem) event.item;
                Control selectedControl = selectedTab.getControl();

                if (selectedTab == helpTab) {
                    // CreateTab이 선택되었을 때의 동작 수행
                    if (selectedControl == null) {
                        helpLayout.draw(helpComposite);
                        selectedTab.setControl(helpComposite);
                    }
                } else if (selectedTab == requireTab) {
                    if (selectedControl == null) {
                        requireLayout.draw(requireComposite);
                        selectedTab.setControl(requireComposite);
                    }
                } else if (selectedTab == explainTab) {
                    if (selectedControl == null) {
                        explainLayout.draw(explainComposite);
                        selectedTab.setControl(explainComposite);
                    }
                } else if (selectedTab == findOSTab) {
                    if (selectedControl == null) {
                        findOSLayout.draw(findOpenSourceComposite);
                        selectedTab.setControl(findOpenSourceComposite);
                    }
                } else if (selectedTab == refactorTab) {
                    if(selectedControl == null) {
                        refactorLayout.draw(refactorComposite);
                        selectedTab.setControl(refactorComposite);
                    }
                } else if (selectedTab == createTab) {
                    if(selectedControl == null) {
                        createLayout.create(createComposite);
                        selectedTab.setControl(createComposite);
                    }
                    
                }

                // 선택된 탭의 컨트롤을 현재 화면에 표시
                tabFolder.setSelection(selectedTab);
                scrolledComposite.setContent(tabFolderComposite);
                scrolledComposite.setMinSize(tabFolderComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
            }

            public void widgetDefaultSelected(SelectionEvent event) {
                // 기본 선택 동작 처리
            }
        });

        gridLayout = new GridLayout(1, false);
        parent.setLayout(gridLayout);

        // 메모리 사용량 표시 레이블과 그래프 영역 생성
        Group group3 = new Group(parent, SWT.NONE);
        data = new GridData(SWT.FILL, SWT.FILL, true, false);
        data.heightHint = 30;
        group3.setLayoutData(data);
        group3.setText("Memory Usage");

        GridLayout group3Layout = new GridLayout(3, false);
        group3.setLayout(group3Layout);

        ansLabel2 = new Label(group3, SWT.WRAP);
        GridData ansLabelData2 = new GridData(SWT.FILL, SWT.FILL, true, true);
        ansLabel2.setLayoutData(ansLabelData2);

        graphCanvas = new Canvas(parent, SWT.NONE);
        data = new GridData(SWT.FILL, SWT.FILL, true, false);
        data.heightHint = 80;
        graphCanvas.setLayoutData(data);
        graphCanvas.addPaintListener(new PaintListener() {
            @Override
            public void paintControl(PaintEvent e) {
                GC gc = e.gc;
                   drawMemoryUsageGraph(gc);
                   gc.dispose(); 
                
            }
        });

        memoryUsageList = new ArrayList<>();
        memoryUsagePercentList = new ArrayList<>();
        logger.log(Level.INFO, "Program started.");
        
     // 메모리 측정 시작 버튼 생성
        startMemoryMeasurementButton = new Button(group3, SWT.PUSH);
        startMemoryMeasurementButton.setText("Start Measurement");
        startMemoryMeasurementButton.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
        startMemoryMeasurementButton.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                startMemoryMeasurement();
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                // Do nothing
            }
        });

        // 메모리 측정 종료 버튼 생성
        stopMemoryMeasurementButton = new Button(group3, SWT.PUSH);
        stopMemoryMeasurementButton.setText("Stop Measurement");
        stopMemoryMeasurementButton.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
        stopMemoryMeasurementButton.setEnabled(false);
        stopMemoryMeasurementButton.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                stopMemoryMeasurement();
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                // Do nothing
            }
        });

        Display.getDefault().timerExec(100, new Runnable() {
            private int lineCount = 0;
            private long accumulatedMemory = 0;

            @Override
            public void run() {
                long totalMemory = Runtime.getRuntime().totalMemory();
                long freeMemory = Runtime.getRuntime().freeMemory();
                long usedMemory = totalMemory - freeMemory;

                accumulatedMemory += usedMemory;
                lineCount++;

                if (lineCount == 100) {
                    double averageMemory = (double) accumulatedMemory / lineCount;
                    double usedMemoryPercent = averageMemory / totalMemory * 100;

                    String memoryUsage = String.format("%.2f", usedMemoryPercent) + "% of Memory Usage";

                    Display.getDefault().asyncExec(() -> {
                        if (graphCanvas != null && !graphCanvas.isDisposed()) {
                            ansLabel2.setText(memoryUsage);
                            graphCanvas.redraw();
                        }
                    });

                    memoryUsageList.add((long) averageMemory);
                    memoryUsagePercentList.add(usedMemoryPercent);

                    lineCount = 0;
                    accumulatedMemory = 0;
                }

                if (!Display.getDefault().isDisposed()) {
                    Display.getDefault().timerExec(100, this);
                }
            }
        });

        gridLayout = new GridLayout(1, false);
        parent.setLayout(gridLayout);
    }

 // 그래프 그리는 메서드
    private void drawMemoryUsageGraph(GC gc) {
        Rectangle clientArea = graphCanvas.getClientArea();


        int graphHeight = clientArea.height - 40; // 그래프 높이를 50만큼 줄임
        int graphBottom = clientArea.y + clientArea.height - 30; // 그래프 아래쪽 y 좌표를 30만큼 줄임
        int graphTop = graphBottom - graphHeight; // 그래프 위쪽 y 좌표 재계산

 

        // y축 제목 그리기
        Font graphFont = new Font(gc.getDevice(), "Arial", 6, SWT.BOLD);
        String yTitle = "Memory Usage (%)";
        if (graphFont == null || graphFont.isDisposed()) {
            graphFont = new Font(gc.getDevice(), "Arial", 6, SWT.BOLD);
        }
        gc.setFont(graphFont);
        graphFont.dispose();

        // yTitle의 높이 계산
        int yTitleHeight = gc.stringExtent(yTitle).y;
        // 눈금 간격과 yTitle의 높이를 고려하여 yTitleY 위치 조정
        int yTitleY = graphTop - yTitleHeight + 5; // yTitle을 약간 위로 이동

        int yTitleX = clientArea.x + clientArea.width - gc.stringExtent(yTitle).x - 5; // 오른쪽 정렬

        gc.drawString(yTitle, yTitleX, yTitleY, true);

        
        // y축 그리기 코드 (변경)
        int numTicks = 5; // y축에 표시할 눈금 개수
        int tickSpacing = graphHeight / (numTicks - 1); // 눈금 간격 계산

        int maxTickValue = 100; // 최대 눈금 값 (0부터 100까지)

        for (int i = 0; i < numTicks; i++) {
            int y = graphBottom - i * tickSpacing;
            gc.setLineWidth(1); // 선 굵기 설정 (1로 변경)
            gc.setLineStyle(SWT.LINE_SOLID); // 선 스타일 설정 추가
            gc.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_BLACK)); // 선 색상 설정 추가
            gc.drawLine(clientArea.x + clientArea.width - 25, y + 10, clientArea.x + clientArea.width - 20, y + 10); // 눈금을 오른쪽에 배치

            // 시작값과 최대 메모리를 기반으로 눈금 값을 계산
            int tickValue = i * (maxTickValue / (numTicks - 1)); // 눈금 값 변경
            String tickLabel = Integer.toString(tickValue); // % 제외하고 눈금 값만 문자열로 변환

            int tickValueX = clientArea.x + clientArea.width - 15; // 눈금 값을 오른쪽으로 정렬하여 조금 더 오른쪽으로 이동
            int tickValueY = y + 5; // 눈금 값의 세로 위치 조정
            gc.drawString(tickLabel, tickValueX, tickValueY, true);

            // 가로선 그리기 코드 추가
            gc.setLineWidth(1); // 가로선 굵기 설정 (1로 변경)
            gc.setLineStyle(SWT.LINE_DOT); // 가로선 스타일 설정 (점선으로 변경)
            gc.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_GRAY)); // 가로선 색상 설정 (연한 회색으로 변경)
            gc.drawLine(clientArea.x + 30, y + 10, clientArea.x + clientArea.width - 20, y + 10); // 가로선 그리기
        }


     // x축 그리기
        gc.setLineStyle(SWT.LINE_SOLID);
        gc.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_BLACK));
        gc.drawLine(clientArea.x + 30, graphBottom + 10, clientArea.x + clientArea.width - 20, graphBottom + 10);

        // x축 제목 그리기
        String xTitle = "Time";
        Font font = new Font(graphCanvas.getDisplay(), "Arial", 5, SWT.BOLD);
        gc.setFont(font);
        int xTitleX = clientArea.x + 10; // 왼쪽 정렬
        int xTitleY = graphBottom + 10; // 아래쪽 여백을 10으로 수정
        gc.drawString(xTitle, xTitleX, xTitleY, true);
        
        font.dispose();
        

        gc.setLineWidth(2);
        // y축 그리기 코드 (변경)
        gc.setLineWidth(1); // 선 굵기 설정 (1로 변경)
        gc.setLineStyle(SWT.LINE_SOLID); // 선 스타일 설정 추가
        gc.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_BLACK)); // 선 색상 설정 추가
        gc.drawLine(clientArea.x + clientArea.width - 20, graphTop + 10, clientArea.x + clientArea.width - 20, graphBottom + 10);
        
     
     // 그래프를 선 그래프로 그리기
        gc.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_BLUE));
        gc.setLineStyle(SWT.LINE_SOLID);
        int prevX = clientArea.x + clientArea.width - 20; // 안전한 값으로 prevX를 초기화합니다.
        int prevY = graphBottom;
        double time = 0;
        double interval = 0;
        
        if (!memoryUsagePercentList.isEmpty()) {
            // 목록에서 값을 가져옵니다.
            prevX = clientArea.x + clientArea.width - 20;
            prevY = graphBottom - (int) (memoryUsagePercentList.get(memoryUsagePercentList.size() - 1) * graphHeight / 100);
            time = memoryUsagePercentList.size() / 10.0;
            interval = (clientArea.width - 50) / (time * 1000);
        }

        
        
        for (int i = memoryUsagePercentList.size() - 2; i >= 0; i--) {
            double x = prevX - i * interval; // x 좌표 계산 (오른쪽 끝에서부터 왼쪽으로)

            // x 좌표가 그래프 영역 안에 있는 경우에만 그래프를 그립니다.
            if (x >= clientArea.x + 30) {
                int y = graphBottom - (int) (memoryUsagePercentList.get(i) * graphHeight / 100);

                // 이전 x 좌표와 현재 x 좌표가 그래프 영역 안에 있는 경우에만 그래프를 그립니다.
                if (prevX >= clientArea.x + 30 && x >= clientArea.x + 30) {
                    gc.drawLine((int) x, y + 10, prevX, prevY + 10);
                }

                // 10초마다 데이터 점을 찍습니다.
                if (i % 10 == 0) {
                    int dotSize = 4;
                    gc.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_RED));
                    gc.fillOval((int) x - dotSize / 2, y + 10 - dotSize / 2, dotSize, dotSize);
                }
                prevX = (int) x;
                prevY = y;
            } else {
                // x 좌표가 그래프 영역을 넘어가면 그래프의 선과 점을 그리지 않습니다.
                break;
            }
        }

        gc.setLineWidth(1);
      

        // 자원 해제
        gc.dispose();
        graphFont.dispose();

        // 자원 해제
        if (graphFont != null && !graphFont.isDisposed()) {
            graphFont.dispose();
        }
    }

        private boolean isMeasuring = false; // 측정 중인지 나타내는 플래그

        // 메모리 측정 시작 메서드
        private void startMemoryMeasurement() {
            isMeasuring = true;
            memoryMeasurementTask = new Runnable() {
                private int lineCount = 0;
                private long accumulatedMemory = 0;

                @Override
                public void run() {
                    if (!isMeasuring) {
                        return;
                    }

                    long totalMemory = Runtime.getRuntime().totalMemory();
                    long freeMemory = Runtime.getRuntime().freeMemory();
                    long usedMemory = totalMemory - freeMemory;

                    accumulatedMemory += usedMemory;
                    lineCount++;

                    if (lineCount == 10) {
                        double averageMemory = (double) accumulatedMemory / lineCount;
                        double usedMemoryPercent = averageMemory / totalMemory * 100;
                        String memoryUsage = String.format("%.2f", usedMemoryPercent) + "% of Memory Usage";

                        Display.getDefault().asyncExec(() -> {
                            if (graphCanvas != null && !graphCanvas.isDisposed()) {
                                ansLabel2.setText(memoryUsage);
                                graphCanvas.redraw();
                            }
                        });

                        memoryUsageList.add((long) averageMemory);
                        memoryUsagePercentList.add(usedMemoryPercent);

                        lineCount = 0;
                        accumulatedMemory = 0;
                    }

                    if (!Display.getDefault().isDisposed()) {
                        Display.getDefault().timerExec(100, this);
                    }
                }
            };
            Display.getDefault().timerExec(100, memoryMeasurementTask);

            // 버튼 상태 변경
            startMemoryMeasurementButton.setEnabled(false);
            stopMemoryMeasurementButton.setEnabled(true);

            // 텍스트 스타일 변경 (기존 스타일로) 및 텍스트 초기화
            Display.getDefault().asyncExec(() -> {
                if (ansLabel2 != null && !ansLabel2.isDisposed()) {
                    Font originalFont = ansLabel2.getFont();
                    if (originalFont != null && !originalFont.isDisposed()) {
                        FontData[] fontData = originalFont.getFontData();

                        // 폰트를 만들고 새로운 폰트에 복사
                        Font newFont = new Font(Display.getDefault(), fontData);

                        try {
                            if (!ansLabel2.isDisposed()) {
                                ansLabel2.setFont(newFont);
                                ansLabel2.setText("");
                                graphCanvas.redraw();
                            }
                        } finally {
                            // 폰트 사용 후 해제
                            if (newFont != null && !newFont.isDisposed()) {
                                newFont.dispose();
                            }
                        }
                    }
                }
            });
        }

        // 메모리 측정 중지 메서드
        private void stopMemoryMeasurement() {
            isMeasuring = false;

            if (memoryMeasurementTask != null) {
                Display.getDefault().timerExec(-1, memoryMeasurementTask);
            }

            // 버튼 상태 변경
            startMemoryMeasurementButton.setEnabled(true);
            stopMemoryMeasurementButton.setEnabled(false);

            // 10초 간격으로 평균 메모리 사용량 계산
            double finalMemoryUsagePercent = calculateAverageMemoryUsage();

            Display.getDefault().asyncExec(() -> {
                if (ansLabel2 != null && !ansLabel2.isDisposed()) {
                    // ansLabel2가 null 또는 disposed되지 않았다면 폰트 정보를 가져오려고 합니다
                    Font originalFont = ansLabel2.getFont();
                    
                    if (originalFont != null && !originalFont.isDisposed()) {
                        FontData[] fontData = originalFont.getFontData();
                        Font newFont = new Font(Display.getDefault(), fontData);

                        try {
                            Display.getDefault().syncExec(() -> {
                                if (!ansLabel2.isDisposed()) {
                                    ansLabel2.setFont(newFont);
                                    ansLabel2.setText("Stopped. Final Memory Usage: " + String.format("%.2f", finalMemoryUsagePercent) + "%");
                                    graphCanvas.redraw();
                                }
                            });
                        } finally {
                            if (newFont != null && !newFont.isDisposed()) {
                                newFont.dispose();
                            }
                        }
                    }
                }
            });
        }

        // 10초 간격으로 평균 메모리 사용량 계산하는 메서드
        private double calculateAverageMemoryUsage() {
            int count = 0;
            long accumulatedMemory = 0;

            // 최근 10초 동안의 데이터를 계산
            for (int i = memoryUsageList.size() - 1; i >= 0 && count < 10; i--) {
                accumulatedMemory += memoryUsageList.get(i);
                count++;
            }

            double averageMemory = (double) accumulatedMemory / count;
            double usedMemoryPercent = averageMemory / Runtime.getRuntime().maxMemory() * 100;

            return usedMemoryPercent;
        }

        @Override
        public void setFocus() {
            // TODO Auto-generated method stub
           }
        
        @Override
        public void dispose() {
             if (ansLabel2 != null && !ansLabel2.isDisposed()) {
                   ansLabel2.dispose();
               }
           
            super.dispose(); // 부모 클래스의 dispose 메서드 호출
        }
        
        
}