import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * No es un test porque no sabía como plantearlo pero es la clase que he utilizado para obtener las personas vecinas del
 * punto que quiero
 */

public class prueba {

    public static void main(String[] args) {

        //Aquí pongo el punto del que quiero obtener sus vecinos, las filas, columnos y el rango de distancia de vecinos
        final List<List<Point>> vecinos = getVecinos(1,1  , 3, 3, 1);

        //Imprimo los vecinos del punto
        vecinos.stream()
                .forEach((points)-> points.stream()
                        .forEach(point-> System.out.println("x: " + point.getX() + " y: "+point.getY())));


    }

    private static List<List<Point>> getVecinos(int i , int j, int rows, int columns,int range){

        return Stream.iterate(i-range, x-> x<=i+range, x-> x+1)
                .filter(x->j<columns)
                .filter(x->i<rows)
                .map(x-> Stream.iterate(j-range, y-> y<=j+range, y->y+1)
                        .filter(b -> x!=i || b!= j )
                        .filter(a-> x>=0 && x<rows)
                        .filter(y-> y>=0 && y<columns)
                        .map(elem-> new Point(x, elem))
                        .collect(Collectors.toUnmodifiableList()))
                .collect(Collectors.toUnmodifiableList());
    }
}


class Point {

    private final int x;
    private final int y;

    public Point(int x, int y){
        this.x =x;
        this.y=y;
    }

    public int getX() {
        return x;
    }

    public int getY(){
        return y;
    }
}
