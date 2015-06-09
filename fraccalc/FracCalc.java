/**
 * 
 */
package fraccalc;


import java.util.Scanner;
import java.util.StringTokenizer;

/**
 * @author merk
 *
 *  NOTES:  This problem cries for Class MixedFraction.  Without using classes,
 *    I just created a bunch of methods for handling the mixedFraction object, 
 *    which is just a string of formats: "n", "n/n" or n_n/n".
 *    The methods validate, extract the total numerator or denominator,
 *    generate mixedFraction string given a total numerator and denominator,
 *    and do math operations on the extracted ints.
 *    
 *    The extra credit piece I did was multiple operations, although I didn't 
 *    implement operator precedence.
 *    
 *    I did some light, but incomplete error handling.
 *    
 *    
 *    hi mark
 */

/*  TEST CASES 
 * 
      1/4 + 1_1/2 ? 1_3/4 
      8/4 + 2 ? 4 
      8/4 + -2 ? 0 
      11/17 - -1/17 ? 12/17 
      1/2 + 2/3 ? 1_1/6 
      -1 * -1/2 ? 1/2 
      3_1/2 * 3_1/4 ? 11_3/8
      5 / 2 ? 2_1/2
      12 / 2_2/5 ? 5
      1_3/8 * 0 ? 0
      19 + 0 ? 19
      0 / 5_1/3 ? 0
      5_1/4 + 3/4 / 1_1/2 ? 4
      1 + 2 + 3 + 4 / 2 ? 5
 *
 */

public class FracCalc
{
  static int doDebug = 0;

  static int BAD_NUM = -2147483648;

  /**
   * @param args
   */
  public static void main(String[] args)
  {
    System.out.print( "Enter input, or 'quit' to exit > " );
    Scanner console = new Scanner( System.in ); 
    String input = console.nextLine();
    System.out.print( input );
    while( ! input.equalsIgnoreCase( "quit") ) {
      debug( 1, "Input is >" + input + "<");
      String result = processInput( input );
      System.out.println("");
      if (result.startsWith( "ERROR") ) {
        System.out.println( result );
      } else {
        System.out.println( input + " ==> " + result );
      }
      System.out.println( "");
      System.out.print( "> ");
      input = console.nextLine();

    }
    
    console.close();
    System.out.println( "");
    System.out.println( "Bye\n" );

  }  // end of main

  
  static String processInput( String input ) {
    StringTokenizer tk = new  StringTokenizer( input );
    String result = null;
    String f1 = null;
    String f2 = null;
    String operator = null;
    int count = tk.countTokens();
    if ((count < 3) || ((count % 2) != 1) ) {
      result = "ERROR: Error parsing input string ["+input+"].";
    } else {
      f1 = tk.nextToken();
      operator = tk.nextToken();
      f2 = tk.nextToken();
      result = calculateResult( f1, operator, f2 );
      while (tk.hasMoreTokens() ) {
        operator = tk.nextToken();
        f2 = tk.nextToken();
        result = calculateResult( result, operator, f2 );
      }
    }
    return result;
  }
  
  
  static String calculateResult( String n1, String operator, String n2) {
    String result = null;
    debug( 1, "calculateResult: n1="+n1+", operator="+operator+", n2="+n2+".");
    if ( ! isValidMixedFract( n1) ) {
      result = "ERROR: invalid mixed fraction ["+n1+"].";
    } else if ( ! isValidMixedFract( n2) ) {
      result = "ERROR: invalid mixed fraction ["+n2+"].";
    } 
    if (result==null)  {  // no errors yet.
      if ("+-*/".contains( operator )) {
        result = operateOnNumbers( n1, n2, operator );
      } else {
        result = "ERROR: invalid operator ["+operator+"].";
      }
    }
    
    return result;
  }
  
  static void debug( int level, String s ) {
    if ( level <= doDebug) {
      System.out.println( "DBG: " + s);
    }
  }
  
  /**
   * 
   * @param fStr
   * @return  true if this is a a valid mixedfraction string
   */
  static boolean isValidMixedFract( String fStr) {
    boolean isValid = true;
    
    int denom = getDenominator( fStr );
    if (denom == BAD_NUM) {
      isValid = false;
    } else {
      int numerator = getNumerator( fStr );
      if (numerator == BAD_NUM) {
        isValid = false;
      }
    }
    return isValid;
  }

  /**
   *   Get the denominator for this mixed fraction.  If there is no
   *   fractional part, the denominator is 1.
   * @param str
   * @return denominator or BAD_NUM on error
   */
  static int getDenominator( String str ) {

    int denominator = BAD_NUM;
    StringTokenizer tk = new  StringTokenizer( str, "_" );
    int count = tk.countTokens();
    if (count==1) {
      String part1 = tk.nextToken();
      denominator = getFractionDenominator( part1, false);
    } else if (count==2) {
      tk.nextToken();
      String part2 = tk.nextToken();
      denominator = getFractionDenominator( part2, true );
    }
    debug( 2, "getDenominator: str ("+str+") den="+denominator);
    return denominator;
  }
 
  /**
   *  Given a fraction, not mixed fraction string (e.g. "1/4" or "5"),
   *    return the denominator.  
   *    IF this string has no "/"
   *      If mustHaveSlash is true return error
   *      else return 1.
   * @param str
   * @param mustHaveSlash
   * @return denominator or BAD_NUM on error
   */
  static int getFractionDenominator( String str, boolean mustHaveSlash ) {
    int denominator = BAD_NUM;
    StringTokenizer fractTk = new  StringTokenizer( str, "/" );
    int count =  fractTk.countTokens();
    if( (count==1) && ! mustHaveSlash) {
      denominator = 1;
    }
    if ( fractTk.countTokens()==2) {
        // make sure numerator is valid
      String numerStr = fractTk.nextToken();
      Integer.valueOf( numerStr );
      String denomStr = fractTk.nextToken();
      denominator = Integer.valueOf(denomStr);
    }
    debug( 3, "getFractionDenominator: str ("+str+") den="+denominator);
    return denominator;
  }
  
  /**
   * Calculate the numerator of a mixed fraction.  This is the total numerator,
   *   so if the string is 4_3/8 the numerator is 35 (4 * 8) + 3
   * @param str
   * @return numerator or BAD_NUM on error
   */
  static int getNumerator( String str ) {
    
    int numerator = BAD_NUM;
    int denominator = getDenominator( str );
    StringTokenizer tk = new  StringTokenizer( str, "_" );
    int count = tk.countTokens();
    if (count==1) {
      numerator = getFractionNumerator( tk.nextToken(), false );
    } else if (count==2) {
      int integerPart = Integer.valueOf( tk.nextToken() );
      String fractional = tk.nextToken();
      int fractNumerator = getFractionNumerator(  fractional, true );
      numerator = fractNumerator + (denominator * integerPart);
    }
    debug( 2, "getNumerator: str ("+str+") num="+numerator);
    return numerator;
 
  }
   
  
  /**
   * Calculate the numerator of a plain, not mixed, fraction string.  
   *    IF this string has no "/" and  mustHaveSlash is true, return error
   * @param str
   * @return numerator or BAD_NUM on error
   */
  static int getFractionNumerator( String str, boolean mustHaveSlash ) {
    
    int numerator = BAD_NUM;
    StringTokenizer tk = new  StringTokenizer( str, "/" );
    int count = tk.countTokens();
    if ((count==1) && ! mustHaveSlash ) {
      numerator = Integer.valueOf( tk.nextToken() );
    } else if (count==2) {
      numerator = Integer.valueOf( tk.nextToken() );
    }
    debug( 3, "getFractionNumerator: str ("+str+") num="+numerator);
    return numerator;
         
  }
  
  /**
   *   Perform an operation on two fractions, given their numerator and denominator values.
   *   
   * @param n1
   * @param n2
   * @param operator
   * @return - mixed fraction string or error message (starting with "ERROR" )
   */
  static String operateOnNumbers( String n1, String n2, String operator) {
    String result = null;
    int numer1 = getNumerator( n1 );
    int denom1 = getDenominator(n1);
    int numer2 = getNumerator( n2 );
    int denom2 = getDenominator(n2);
    
    int totalNumer=0, totalDenom=1;  // only initialize for avoid initliazation error later in method.
    if (operator.equals( "+" )) {
        totalDenom = denom1 * denom2;
        totalNumer = (numer1 * denom2) + (numer2 * denom1);
    } else if (operator.equals( "-" )) {
      totalDenom = denom1 * denom2;
      totalNumer = (numer1 * denom2) - (numer2 * denom1);
    }  else if (operator.equals( "*" )) {
      totalDenom = denom1 * denom2;
      totalNumer = numer1 * numer2;
    }
    else if (operator.equals( "/" )) {
      totalDenom = numer2 * denom1;
      totalNumer = numer1 * denom2;
    } else {
      result = "ERROR: illegal operator ["+operator+"].";
    }
    if (result == null) {
      result = createMixedFraction( totalNumer, totalDenom );
    }
    return result;
  }
  

 /**
  *   Generate a valid, fully reduced mixed fraction string for the given numerator and denominator.
  *   
  * @param numerator
  * @param denominator
  * @return  mixed fraction string
  */
 static String createMixedFraction( int numerator, int denominator ) {
    String reduced = "";
    int gcdVal = gcd( numerator, denominator );
    debug( 1, "createMixedFraction: num="+numerator+", denom="+denominator+",  gcd="+ gcdVal);
    if (gcdVal == 0) gcdVal=1;
    numerator /= gcdVal;
    denominator /= gcdVal;
    if (numerator == 0) {
      reduced = "0";
    } else {
     
      int part1 = numerator /denominator;
      int part2denom = numerator % denominator;
      reduced = "";
      if (part1!=0) {
        reduced = part1 + "";
        if (part2denom != 0) {
          reduced += "_";
        }
      } 
      if (part2denom != 0) {
        reduced += part2denom + "/" + denominator; 
      }
    }
    return reduced;
  }
  
  /**
   * calculate the greatest common denominator between two numbers.
   * @param a
   * @param b
   * @return
   */
  static int gcd(int a, int b)
  {
    if(a == 0 || b == 0) return a+b; // base case
    return gcd(b,a%b);
  }

  
}  // end of FracCalc
