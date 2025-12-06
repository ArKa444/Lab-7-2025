
import functions.*;
import functions.basic.*;
import java.util.Iterator;

public class Main {
    public static void main(String[] args) {
        //тестирование 1("Итератор")
        System.out.println("Тестирование итераторов");
        testIterators();

        //тестирование 2("Фабричный метод")
        System.out.println("\nТестирование фабрик");
        testFactories();

        //тестирование 3(рефлексия)
        System.out.println("\n Рефлексия");
        testReflection();
    }

    private static void testIterators() {
        //создаем ArrayTabulatedFunction для тестирования
        System.out.println("ArrayTabulatedFunction:");
        TabulatedFunction arrayFunc = new ArrayTabulatedFunction(0, 8, 5);
        
        //используем for-each
        for (FunctionPoint p : arrayFunc) {
            System.out.println("  " + p);
        }

        //создаем LinkedListTabulatedFunction для тестирования
        System.out.println("LinkedListTabulatedFunction:");
        TabulatedFunction listFunc = new LinkedListTabulatedFunction(0, 8, 5);
        
        //for-each цикл для связного списка
        for (FunctionPoint p : listFunc) {
            System.out.println("  " + p);
        }

        //явный итератор
        System.out.println("Явный итератор:");
        Iterator<FunctionPoint> iterator = arrayFunc.iterator();
        while (iterator.hasNext()) {
            System.out.println("  " + iterator.next());
        }
    }

    private static void testFactories() {
        
        System.out.println("Фабрика по умолчанию (Array):");
        TabulatedFunction tf1 = TabulatedFunctions.tabulate(new Cos(), 0, Math.PI, 5);
        System.out.println("  Функция: " + tf1);

        //меняем фабрику на LinkedListTabulatedFunctionFactory
        System.out.println("Установка LinkedList фабрики:");
        TabulatedFunctions.setTabulatedFunctionFactory(new LinkedListTabulatedFunction.LinkedListTabulatedFunctionFactory());
        TabulatedFunction tf2 = TabulatedFunctions.tabulate(new Cos(), 0, Math.PI, 5);
        System.out.println("  Функция: " + tf2);

        //возвращаем фабрику ArrayTabulatedFunctionFactory
        System.out.println("Возврат к Array фабрике:");
        TabulatedFunctions.setTabulatedFunctionFactory(new ArrayTabulatedFunction.ArrayTabulatedFunctionFactory());
        TabulatedFunction tf3 = TabulatedFunctions.tabulate(new Cos(), 0, Math.PI, 5);
        System.out.println("  Функция: " + tf3);
    }

    private static void testReflection() {
        System.out.println("Тестирование рефлексии");

        //тест 1:ArrayTabulatedFunction через рефлексию
        System.out.println("1. Создание ArrayTabulatedFunction через рефлексию:");
        TabulatedFunction f1 = TabulatedFunctions.createTabulatedFunction(ArrayTabulatedFunction.class, 0, 8, 5);
        System.out.println("  Класс: " + f1.getClass().getSimpleName());
        System.out.println("  Значения: " + f1);

        //тест 2:с массивом значений Y
        System.out.println("2. Создание ArrayTabulatedFunction через рефлексию:");
        TabulatedFunction f2 = TabulatedFunctions.createTabulatedFunction(ArrayTabulatedFunction.class, 0, 8, new double[] {0, 8});
        System.out.println("  Класс: " + f2.getClass().getSimpleName());
        System.out.println("  Значения: " + f2);

        //тест 3:LinkedListTabulatedFunction через рефлексию
        System.out.println("3. Создание через рефлексию из массива точек:");
        TabulatedFunction f3 = TabulatedFunctions.createTabulatedFunction( LinkedListTabulatedFunction.class, new FunctionPoint[] {new FunctionPoint(0, 0), new FunctionPoint(8, 64)});
        System.out.println("  Класс: " + f3.getClass().getSimpleName());
        System.out.println("  Значения: " + f3);

        //тест 4: таб. с исп. рефлексии
        System.out.println("4. Табулирование с рефлексией:");
        TabulatedFunction f4 = TabulatedFunctions.tabulate( LinkedListTabulatedFunction.class, new Sin(), 0, Math.PI, 11);
        System.out.println("  Класс: " + f4.getClass().getSimpleName());
        System.out.println("  Значения: " + f4);
    }
}