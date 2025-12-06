package functions;

public interface TabulatedFunctionFactory {
    TabulatedFunction createTabulatedFunction(double leftX, double rightX, int pointsCount);//создает ф-ию по границам и кол-ву точек
    TabulatedFunction createTabulatedFunction(double leftX, double rightX, double[] values);//создает ф-ию по границам и массиву Y
    TabulatedFunction createTabulatedFunction(FunctionPoint[] points);//создает ф-ию по массиву точек
}
