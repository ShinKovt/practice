import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.renderer.xy.XYSplineRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeriesCollection;
//класс наследующий класс ApplicationFrame который служит для создания и отображения окна
public class App extends ApplicationFrame {
    //массив из 21 элемента содержащий координаты по иксу для которых будет вычисленно значение ф-ии
    private static double[] Ox = new double[21];
    // список содержащий массивы координат по игреку вычисленных для указанных разибений при этом к-во массивов в списке
    // на единицу больше, чем разбиений для которых считаем по причине того, что последний массив содержит координаты
    // по игрек для аналитически вычисленного интеграла
    private static List<double[]> nOy;
    //нижний предел интегрирования
    private static double a = 0;
    //верхний предел интегрирования
    private static double b = Math.PI;
    //нижния и верхняя грань
    private static double x_high, x_low;
    //к-во разбиений
    private static int count_of_split;
    //массив из количесвта count_of_split значений разбиений для которых будем считать по методу левых прямоугольников
    private static int[] n;
    //массив из к-ва count_of_split значений максимальной невязки для соответсвующий значений
    private static double[] difference;

    //метод возвращающий вычисленное значение подынтегральной ф-ии в точке х при заданном t
    static double Function(double x, double t) {
        return Math.pow(x - t, 2) * Math.cos(x * t);
    }
    //метод возвращающий значение вычисленной первообразной в точке х при заданном t
    static double antiderivative(double x, double t) {
        return  ((Math.pow(t, 2) * Math.pow(x, 2) - 2 * t * Math.pow(x, 3) + Math.pow(x, 4) - 2) * Math.sin(t * x)
                + 2 * x * (t - x) * Math.cos(t * x)) / Math.pow(x, 3);
    }
    //метод левых прямоугольников
    static double leftRectangles(double x, double n, double h, double a) {
        double sum = 0;
        for (int i = 0; i < n; i++)
            sum += h * (Function(x, a + h * i));
        return sum;
    }
    //метод который возвращает строку заголовка таблицы
    static String printHead(int n_count, int[] n) {
        String tmp = "\n" + "X\t\t";
        for (int i = 0; i < n_count; i++) {
            tmp += "N = " + n[i] + "\t\t";
        }
        tmp += "\n";
        return tmp;
    }
    //конструктор класса принимающий в качестве параметров строку с заголовком окна и заголовком над графиком
    public App(String applicationTitle, String chartTitle) {
        //вызывает уже определенные в унаследованном от ApplicationFrame порядок установке значений в констркукторе от
        //супер класса
        super(applicationTitle);
        //создаём объект графика передаём в Factory заголовок над графиком наименование осей созданный набор графиков
        //ориентацию подписей, показ легенды и остальные параметры как в официальном примере документации
        JFreeChart xylineChart = ChartFactory.createXYLineChart(
                chartTitle,
                "x",
                "y",
                createDataset(),
                PlotOrientation.VERTICAL ,true,true,false);

        //создаём панель для отображения объекта графикаи передаем в конструкор созданный ранее объект графика
        ChartPanel chartPanel = new ChartPanel(xylineChart);
        //устанавливаем размер панели с графиком используя встроенный отдельный объект из пакета awt являющийся
        //контейнером для хранения двух целочисленных значений которые служат для обозначения размеров некоего объекта
        //из графической составляющей приложения
        chartPanel.setPreferredSize(new Dimension(560 , 367));
        //так указано в документации
        final XYPlot plot = xylineChart.getXYPlot();

        //создаём объект renderer который будет рисовать объект на панели будем использовать режим(объект его
        // реализующий) с плавным соединением точек по некоторой интерполяции
        XYSplineRenderer renderer = new XYSplineRenderer();
        //вызываем метод добавления линий наших графиков к объекту renderer
        addLine(renderer);
        //устанавливаем renderer как способ отображения графиков
        plot.setRenderer(renderer);
        //добавляем панель с графиком в окно
        setContentPane(chartPanel);
    }
    //метод добавления линий графиков в объект renderer
    public void addLine(XYSplineRenderer renderer) {
        for(int i = 0; i <= count_of_split; ++i){
            //устанавливаем для i-го графика цвет
            renderer.setSeriesPaint(i, generateColor());
            //аналитическое значение графика сделаем более "жирным" (5px)
            if(i == count_of_split){
                renderer.setSeriesStroke(i , new BasicStroke( 5.0f ) );
            }
            else {
                renderer.setSeriesStroke(i , new BasicStroke( 3.0f ) );
            }
        }

    }
    //генерируем случайный цвет для графика используя в качестве объекта рандома объект созданный при запуске программы
    //в главном потоке
    public Color generateColor(){
        return new Color(ThreadLocalRandom.current().nextInt(0, 255),
                         ThreadLocalRandom.current().nextInt(0, 255),
                         ThreadLocalRandom.current().nextInt(0, 255));
    }
    //метод создания набора значений для объекта графика (объекта графического отображения)
    private XYDataset createDataset( ) {
        List<XYSeries> lines = new ArrayList<>();
        for(int i = 0; i < count_of_split; ++i) {
            lines.add(new XYSeries("N="+ n[i]));
            for(int j = 0; j < 20; ++j) {
                lines.get(i).add(Ox[j], nOy.get(i)[j]);
            }
        };
        lines.add(new XYSeries("Analytical"));
        for(int j = 0; j < 20; ++j) {
            lines.get(lines.size() - 1).add(Ox[j], nOy.get(lines.size() - 1)[j]);
        }
        //упаковываем отдельные объекты содержащий информацию по каждому отдельно взятому графику в объект коллекции
        //используемой для создания объекта графика (объекта графического отображения)
        final XYSeriesCollection dataset = new XYSeriesCollection();
        for(int i = 0; i < lines.size(); ++i) {
            dataset.addSeries(lines.get(i));
        }
        return dataset;
    }

    //функция main(отсюда идёт выполение кода)
    public static void main(String[] args) {
        //создаём новый объект списка для хранения значений функции
        nOy = new ArrayList<>();
        //создаём объект для считывания данных с клавиатуры
        Scanner scanner = new Scanner(System.in);
        System.out.println("Введите нижнюю границу x: ");
        x_low = scanner.nextDouble();
        System.out.println("Введите верхнюю границу x: ");
        x_high = scanner.nextDouble();
        System.out.println("Введите количество разбиений: ");
        count_of_split = scanner.nextInt();
        n = new int[count_of_split];
        difference = new double[count_of_split];

        System.out.println("\n");
        for (int i = 0; i < count_of_split; i++) {
            System.out.println("Введите разбиение №" + (i + 1) + ": ");
            n[i] = scanner.nextInt();
        }
        //заполняем список содержащий значения функций массивами размерами 21 (потому что от 0 до 20 включительно)
        for(int i = 0; i <= count_of_split; ++i){
            nOy.add(new double[21]);
        }
        //строка в которую будет записываться информация для вывода в консоль
        String res = printHead(count_of_split, n);
        //пробегаемся по заданому диапазону i для вычисления координат по оси х
        for (int i = 0; i <= 20; i++) {
            //вычисляем икс согласно данной нам формуле (x = c + i * (d-c)/20)
            double x = x_low + i * (x_high - x_low) / 20;
            Ox[i] = x;
            double analiticRes = antiderivative(x, b) - antiderivative(x, a);
            res += (x) + "\t";
            //заполняем список с значениями вычисленных функций значением точного значения интеграла в данной точке
            //(массив для хранения находится в самом конце списка)
            nOy.get(nOy.size() - 1)[i] = analiticRes;

            for (int k = 0; k < count_of_split; k++) {
                //вычисляем шаг разбиения
                double h = (b - a) / n[k];
                //вычисляем значение функции методом левых прямоугольников
                double leftRectRes = leftRectangles(x, n[k], h, a);
                //сравниваем и кладём максимальную невязку в данной точке
                if (Math.abs(analiticRes - leftRectRes) > difference[k])
                    difference[k] = Math.abs(analiticRes - leftRectRes);

                res += (leftRectRes) + "\t";
                //заполняем список приближённо вычисленным значением
                nOy.get(k)[i] = leftRectRes;
            }

            res += "\n";

        }
        res += "Mаксимальная невязка равна: \n";
        for (int i = 0; i < count_of_split; i++)
            res += n[i] + ": " + difference[i] + "\n";


        System.out.println(res);

        //создаём объект приложения
        App chart = new App("(x - t)^2 * cos(x * t)",
                "(x - t)^2 * cos(x * t)");
        //упаковываем объект графика
        chart.pack( );
        //центрируем график в окне
        RefineryUtilities.centerFrameOnScreen(chart);
        //делаем окно видимым для пользователя
        chart.setVisible(true);
    }

}
