import java.util.ArrayList;

class ResultFromScala {
    ArrayList<ArrayList<Integer>> _pixels;
    String _error;

    ResultFromScala(ArrayList<ArrayList<Integer>> pixels, String error) {
        _pixels = pixels;
        _error = error;
    }

    boolean errorExist() {
        return _error.length() > 0;
    }
    
    String getError() {
        return _error;
    }
    
    ArrayList<ArrayList<Integer>> getPixels() {
        return _pixels;
    }
}
