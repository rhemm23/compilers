struct c {
  int z;
}

struct data {
  int a;
  bool b;
  bool data;
  struct c x;
}

struct data1 {
  bool c;
}

void test3() {
  int a;
}

void test() {
  int a;
  receive >> a;
}

int test1(int a, bool b, int c) {
  print << "hello!";
  ret;            /// Missing return value
  ret fls;        /// Bad return value
}

void main() {

  bool d;
  struct data c;
  struct data a;

  print << main;    /// Attempt to write function
  print << data;    /// Attempt to write struct name
  print << a;       /// Attemp to write struct variable
  print << test();  /// Attempt to write void
  print << 2;

  receive >> main;  /// Attempt to read function
  receive >> data;  /// Attempt to read struct name
  receive >> a;     /// Attempt to read struct variable

  a();              /// Attempt to call non-function
  test(1);          /// Function call with wrong number of args
  test1(a.a + fls, 33, tru);  /// Arithmetic operator applied to non-numeric operand
                              /// Type of actual does not match type of formal * 2
  ret a.b;          /// Return with value in void function
  ret fls > tru;    /// Relational operator applied to non-numeric operand * 2
                    /// Return with value in void function
  ret fls < tru;    /// Relational operator applied to non-numeric operand * 2
                    /// Return with value in void function
  ret fls <= tru;   /// Relational operator applied to non-numeric operand * 2
                    /// Return with value in void function
  ret fls >= tru;   /// Relational operator applied to non-numeric operand * 2
                    /// Return with value in void function

  a = fls + "";     /// Arithmetic operator applied to non-numeric operand * 2
  a = fls * "";     /// Arithmetic operator applied to non-numeric operand * 2
  a = fls / tru;    /// Arithmetic operator applied to non-numeric operand * 2
  a = tru - tru;    /// Arithmetic operator applied to non-numeric operand * 2

  d = (8 && fls);    /// Logical operator applied to non-bool operand
  a = tru || "test"; /// Logical operator applied to non-bool operand
  a = !"test";       /// Logical operator applied to non-bool operand
  if (89) { }        /// Non-bool expression used as if condition
  if(a) {            /// Non-bool expression used as if condition
    print << "x";
  } else {
    print << "y";
  }

  while("test") { }   /// Non-bool expression used as if condition
  repeat(fls) { }     /// Non-integer expression used as repeat clause

  a = tru == 8;       /// Type mismatch
  d = fls != "t";     /// Type mismatch
  a = (d = 8);        /// Type mismatch

  d = test() == test3();  /// Equality operator applied to void functions
  d = test == test3;      /// Equality operator applied to functions
  d = data == data1;      /// Equality operator applied to struct names
  d = a == c;             /// Equality operator applied to struct variables

  d = (test = test3);     /// Function assignment
  d = (data = data1);     /// Struct name assignment
  a = c;                  /// Struct variable assignment
}