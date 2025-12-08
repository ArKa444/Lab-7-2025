package functions;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class TabulatedFunctions {
    //статическое поле фабрики (ArrayTabulatedFunction по умолчанию)
    private static TabulatedFunctionFactory factory =
            new ArrayTabulatedFunction.ArrayTabulatedFunctionFactory();

    //Конструктор(запрещаем создание объектов)
    private TabulatedFunctions() {
        throw new AssertionError("Нельзя создавать объекты TabulatedFunctions");
    }

    //метод для смены фабрики (меняет тип создаваемых объектов)
    public static void setTabulatedFunctionFactory(TabulatedFunctionFactory factory) {
        TabulatedFunctions.factory = factory;
    }

    //методы создания через фабрику (вызывают фабрику)
    public static TabulatedFunction createTabulatedFunction(double leftX, double rightX, int pointsCount) {
        return factory.createTabulatedFunction(leftX, rightX, pointsCount);
    }

    public static TabulatedFunction createTabulatedFunction(double leftX, double rightX, double[] values) {
        return factory.createTabulatedFunction(leftX, rightX, values);
    }

    public static TabulatedFunction createTabulatedFunction(FunctionPoint[] points) {
        return factory.createTabulatedFunction(points);
    }

    //методы создания через рефлексию (проверка, что класс реализует TabulatedFunction)
    public static TabulatedFunction createTabulatedFunction(Class<? extends TabulatedFunction> clazz, double leftX, double rightX, int pointsCount) {
        //проверка параметров
        if (clazz == null) {
            throw new IllegalArgumentException("Некорректный класс");
        }
        if (pointsCount < 2) {
            throw new IllegalArgumentException("Требуется минимум 2 точки");
        }

        try {
            //ищем конструктор с параметрами
            Constructor<? extends TabulatedFunction> constructor = clazz.getConstructor(double.class, double.class, int.class);
            //создаем объект через рефлексию
            return constructor.newInstance(leftX, rightX, pointsCount);
        }
        catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new IllegalArgumentException("Сбой при рефлексивном создании", e);
        }
    }

    public static TabulatedFunction createTabulatedFunction(Class<? extends TabulatedFunction> clazz, double leftX, double rightX, double[] values) {
        //проверка параметров
        if (clazz == null || values == null) {
            throw new IllegalArgumentException("Некорректные параметры");
        }

        try {
            //ищем конструктор с параметрами 
            Constructor<? extends TabulatedFunction> constructor = clazz.getConstructor(double.class, double.class, double[].class);
            return constructor.newInstance(leftX, rightX, values);
        }
        catch (NoSuchMethodException | InstantiationException |
               IllegalAccessException | InvocationTargetException e) {
            throw new IllegalArgumentException("Сбой при рефлексивном создании", e);
        }
    }

    public static TabulatedFunction createTabulatedFunction(Class<? extends TabulatedFunction> clazz, FunctionPoint[] points) {
        //проверка параметров
        if (clazz == null || points == null) {
            throw new IllegalArgumentException("Некорректные параметры");
        }

        try {
            //онструктор с параметром FunctionPoint[]
            Constructor<? extends TabulatedFunction> constructor = clazz.getConstructor(FunctionPoint[].class);
            return constructor.newInstance((Object)points);
        }
        catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new IllegalArgumentException("Сбой при рефлексивном создании", e);
        }
    }

    //таб. ф-ии (через фабрику)
    public static TabulatedFunction tabulate(Function function, double leftX, double rightX, int pointsCount) {
        //проверка параметров
        if (function == null) {
            throw new IllegalArgumentException("Функция не задана");
        }
        if (leftX >= rightX) {
            throw new IllegalArgumentException("Неверный интервал");
        }
        if (pointsCount < 2) {
            throw new IllegalArgumentException("Требуется минимум 2 точки");
        }

        //создаем массив точек
        double step = (rightX - leftX) / (pointsCount - 1);
        FunctionPoint[] points = new FunctionPoint[pointsCount];

        //табулируем ф-ию
        for (int i = 0; i < pointsCount; i++) {
            double x = leftX + i * step;
            double y = function.getFunctionValue(x);
            points[i] = new FunctionPoint(x, y);
        }

        //используем createTabulatedFunction()
        return createTabulatedFunction(points);
    }

    //табулирование через рефлексию
    public static TabulatedFunction tabulate(Class<? extends TabulatedFunction> clazz, Function function, double leftX, double rightX, int pointsCount) {
        //проверка параметров
        if (clazz == null || function == null) {
            throw new IllegalArgumentException("Некорректные параметры");
        }
        if (leftX >= rightX) {
            throw new IllegalArgumentException("Неверный интервал");
        }
        if (pointsCount < 2) {
            throw new IllegalArgumentException("Требуется минимум 2 точки");
        }

        //создаем массив точек
        double step = (rightX - leftX) / (pointsCount - 1);
        FunctionPoint[] points = new FunctionPoint[pointsCount];

        //табулируем ф-ию
        for (int i = 0; i < pointsCount; i++) {
            double x = leftX + i * step;
            double y = function.getFunctionValue(x);
            points[i] = new FunctionPoint(x, y);
        }

        //создаем ф-ию через рефлексию
        return createTabulatedFunction(clazz, points);
    }

    //вывод таб. ф-ии в поток (pointsCount, x1, y1, x2, y2, ...)
    public static void outputTabulatedFunction(TabulatedFunction function, OutputStream out) throws IOException {
        DataOutputStream dataOut = new DataOutputStream(out);
        int pointsCount = function.getPointsCount();
        dataOut.writeInt(pointsCount);

        //записываем координаты всех точек
        for (int i = 0; i < pointsCount; i++) {
            dataOut.writeDouble(function.getPointX(i));
            dataOut.writeDouble(function.getPointY(i));
        }

        dataOut.flush();
    }

    //запись таб. ф-ии в символьный поток
    public static void writeTabulatedFunction(TabulatedFunction function, Writer out) throws IOException {
        out.write(String.valueOf(function.getPointsCount()));
        out.write(" ");

        //записываем координаты через пробелы
        for (int i = 0; i < function.getPointsCount(); i++) {
            out.write(String.valueOf(function.getPointX(i)));
            out.write(" ");
            out.write(String.valueOf(function.getPointY(i)));
            out.write(" ");
        }

        out.write("\n"); //перенос строки
        out.flush();
    }

    //ввод таб. ф-ии (через фабрику)
    public static TabulatedFunction inputTabulatedFunction(InputStream in) throws IOException {
        DataInputStream dataIn = new DataInputStream(in);
        int pointsCount = dataIn.readInt();

        if (pointsCount < 2) {
            throw new IllegalArgumentException("Недостаточно данных");
        }

        FunctionPoint[] points = new FunctionPoint[pointsCount];
        for (int i = 0; i < pointsCount; i++) {
            double x = dataIn.readDouble();
            double y = dataIn.readDouble();
            points[i] = new FunctionPoint(x, y);
        }

        
        return createTabulatedFunction(points);
    }

    //ввод таб. ф-ии из потока через рефлексию
    public static TabulatedFunction inputTabulatedFunction(Class<? extends TabulatedFunction> clazz, InputStream in) throws IOException {
        if (clazz == null) {
            throw new IllegalArgumentException("Класс отсутствует");
        }

        DataInputStream dataIn = new DataInputStream(in);
        int pointsCount = dataIn.readInt();

        if (pointsCount < 2) {
            throw new IllegalArgumentException("Недостаточно данных");
        }

        FunctionPoint[] points = new FunctionPoint[pointsCount];
        for (int i = 0; i < pointsCount; i++) {
            double x = dataIn.readDouble();
            double y = dataIn.readDouble();
            points[i] = new FunctionPoint(x, y);
        }

        //создаем ф-ию через рефлексию
        return createTabulatedFunction(clazz, points);
    }

    //чтение таб. ф-ии (через фабрику)
    public static TabulatedFunction readTabulatedFunction(Reader in) throws IOException {
        StreamTokenizer st = new StreamTokenizer(in);
        st.nextToken();
        int pointsCount = (int) st.nval;

        if (pointsCount < 2) {
            throw new IllegalArgumentException("Недостаточно данных");
        }

        FunctionPoint[] points = new FunctionPoint[pointsCount];
        for (int i = 0; i < pointsCount; i++) {
            st.nextToken();
            double x = st.nval;
            st.nextToken();
            double y = st.nval;
            points[i] = new FunctionPoint(x, y);
        }

        //используем createTabulatedFunction()
        return createTabulatedFunction(points);
    }

    //чтение таб. ф-ии из симв. потока через рефлексию
    public static TabulatedFunction readTabulatedFunction(Class<? extends TabulatedFunction> clazz, Reader in) throws IOException {
        if (clazz == null) {
            throw new IllegalArgumentException("Класс отсутствует");
        }

        StreamTokenizer st = new StreamTokenizer(in);
        st.nextToken();
        int pointsCount = (int) st.nval;

        if (pointsCount < 2) {
            throw new IllegalArgumentException("Недостаточно данных");
        }

        FunctionPoint[] points = new FunctionPoint[pointsCount];
        for (int i = 0; i < pointsCount; i++) {
            st.nextToken();
            double x = st.nval;
            st.nextToken();
            double y = st.nval;
            points[i] = new FunctionPoint(x, y);
        }

        //создаем ф-ию через рефлексию
        return createTabulatedFunction(clazz, points);
    }
}