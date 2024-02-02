import java.math.BigDecimal;

/**
 * @Author lnd
 * @Description
 * @Date 2024/2/1 14:48
 */
public class Test {
    public static void main(String[] args) {
        BigDecimal value = new BigDecimal(0.00);
        boolean b = BigDecimal.ZERO.compareTo(value) < 0;
        System.out.println(b);
    }
}
