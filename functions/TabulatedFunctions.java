package functions;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class TabulatedFunctions {
    //Конструктор(запрещаем создание объектов)
    private TabulatedFunctions() {
        throw new AssertionError("Нельзя создавать объекты класса TabulatedFunctions");
    }
    
    
    //фабрика по умолчанию (ArrayTabulatedFunction)
    private static TabulatedFunctionFactory factory =new ArrayTabulatedFunction.ArrayTabulatedFunctionFactory();
    
    //метод длясмены фабрики 
    public static void setTabulatedFunctionFactory(TabulatedFunctionFactory factory) {
        TabulatedFunctions.factory = factory;
    }
    
    //методы создания через фабрику
    public static TabulatedFunction createTabulatedFunction(double leftX, double rightX, int pointsCount) {
        return factory.createTabulatedFunction(leftX, rightX, pointsCount);
    }
    
    public static TabulatedFunction createTabulatedFunction(double leftX, double rightX, double[] values) {
        return factory.createTabulatedFunction(leftX, rightX, values);
    }
    
    public static TabulatedFunction createTabulatedFunction(FunctionPoint[] points) {
        return factory.createTabulatedFunction(points);
    }
    
    //методы создания через рефлекси
    public static TabulatedFunction createTabulatedFunction(Class<? extends TabulatedFunction> functionClass,double leftX, double rightX, int pointsCount) {
        try { //ищем конструктор с параметрами (double, double, int)
            Constructor<? extends TabulatedFunction> constructor =functionClass.getConstructor(double.class, double.class, int.class);
            return constructor.newInstance(leftX, rightX, pointsCount);//создаем объект
        } catch (Exception e) {
            throw new IllegalArgumentException("Ошибка создания функции", e);// исключение IllegalArgumentException
        }
    }
    
    public static TabulatedFunction createTabulatedFunction(Class<? extends TabulatedFunction> functionClass,double leftX, double rightX, double[] values) {
        try {
            Constructor<? extends TabulatedFunction> constructor =functionClass.getConstructor(double.class, double.class, double[].class);
            return constructor.newInstance(leftX, rightX, values);
        } catch (Exception e) {
            throw new IllegalArgumentException("Ошибка создания функции", e);
        }
    }
    
    public static TabulatedFunction createTabulatedFunction(Class<? extends TabulatedFunction> functionClass,FunctionPoint[] points) {
        try {
            Constructor<? extends TabulatedFunction> constructor =functionClass.getConstructor(FunctionPoint[].class);
            return constructor.newInstance((Object)points);
        } catch (Exception e) {
            throw new IllegalArgumentException("Ошибка создания функции", e);
        }
    }
    
    //табулирование через рефлексию (задание 3)
    public static TabulatedFunction tabulate(Class<? extends TabulatedFunction> functionClass,Function function, double leftX, double rightX, int pointsCount) {
        if (leftX < function.getLeftDomainBorder() || rightX > function.getRightDomainBorder()) {
            throw new IllegalArgumentException("Границы табулирования выходят за область определения");
        }
        
        if (pointsCount < 2) {
            throw new IllegalArgumentException("Количество точек должно быть не менее 2");
        }
        //создаем массив значений Y
        double[] values = new double[pointsCount];
        double step = (rightX - leftX) / (pointsCount - 1);
        //табулируем ф-ию
        for (int i = 0; i < pointsCount; i++) {
            double x = leftX + i * step;
            values[i] = function.getFunctionValue(x);
        }
        //создаем ф-ию
        return createTabulatedFunction(functionClass, leftX, rightX, values);
    }
    
    public static TabulatedFunction tabulate(Function function, double leftX, double rightX, int pointsCount) {
        if (leftX < function.getLeftDomainBorder() || rightX > function.getRightDomainBorder()) {
            throw new IllegalArgumentException("Границы табулирования [" + leftX + ", " + rightX + "] " + "выходят за область определения функции [" + function.getLeftDomainBorder() + ", " + function.getRightDomainBorder() + "]");
        }
        
        if (pointsCount < 2) {
            throw new IllegalArgumentException("Количество точек должно быть не менее 2");
        }
        
        double[] values = new double[pointsCount];
        double step = (rightX - leftX) / (pointsCount - 1);
        
        for (int i = 0; i < pointsCount; i++) {
            double x = leftX + i * step;
            values[i] = function.getFunctionValue(x);
        }
        
        //заменяем new ArrayTabulatedFunction на вызов фабрики
        return createTabulatedFunction(leftX, rightX, values);
    }
    
    public static TabulatedFunction inputTabulatedFunction(InputStream in) {
        try (DataInputStream dis = new DataInputStream(in)) {
            int pointsCount = dis.readInt();
            
            FunctionPoint[] points = new FunctionPoint[pointsCount];
            for (int i = 0; i < pointsCount; i++) {
                double x = dis.readDouble();
                double y = dis.readDouble();
                points[i] = new FunctionPoint(x, y);
            }
            
            //заменяем new ArrayTabulatedFunction на вызов фабрики
            return createTabulatedFunction(points);
            
        } catch (IOException e) {
            throw new RuntimeException("Ошибка ввода функции", e);
        }
    }
    
    public static TabulatedFunction readTabulatedFunction(Reader in) {
        try (BufferedReader reader = new BufferedReader(in)) {
            StreamTokenizer tokenizer = new StreamTokenizer(reader);
            tokenizer.parseNumbers();
            
            if (tokenizer.nextToken() != StreamTokenizer.TT_NUMBER) {
                throw new RuntimeException("Ожидалось количество точек");
            }
            int pointsCount = (int) tokenizer.nval;
            
            FunctionPoint[] points = new FunctionPoint[pointsCount];
            for (int i = 0; i < pointsCount; i++) {
                if (tokenizer.nextToken() != StreamTokenizer.TT_NUMBER) {
                    throw new RuntimeException("Ожидалась координата X");
                }
                double x = tokenizer.nval;
                
                if (tokenizer.nextToken() != StreamTokenizer.TT_NUMBER) {
                    throw new RuntimeException("Ожидалась координата Y");
                }
                double y = tokenizer.nval;
                
                points[i] = new FunctionPoint(x, y);
            }
            
            //заменяем new ArrayTabulatedFunction на вызов фабрики
            return createTabulatedFunction(points);
            
        } catch (IOException e) {
            throw new RuntimeException("Ошибка чтения функции", e);
        }
    }
    
    public static void outputTabulatedFunction(TabulatedFunction function, OutputStream out) {
        try (DataOutputStream dos = new DataOutputStream(out)) {
            int pointsCount = function.getPointsCount();
            dos.writeInt(pointsCount);
           
            for (int i = 0; i < pointsCount; i++) {
                dos.writeDouble(function.getPointX(i));
                dos.writeDouble(function.getPointY(i));
            }
            
            dos.flush();
        } catch (IOException e) {
            throw new RuntimeException("Ошибка вывода функции", e);
        }
    }
    
    public static void writeTabulatedFunction(TabulatedFunction function, Writer out) {
        PrintWriter writer = new PrintWriter(new BufferedWriter(out));
        int pointsCount = function.getPointsCount();
        writer.print(pointsCount + " ");
        
        for (int i = 0; i < pointsCount; i++) {
            writer.print(function.getPointX(i) + " " + function.getPointY(i) + " ");
        }
        
        writer.flush();
    }
}