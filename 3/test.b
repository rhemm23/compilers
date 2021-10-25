
### Test various variable declarations
int a;
void b;
bool c;
struct m d;

### Test declaring struct(structDecl) with multiple variables(structBody)
struct name {
  struct m val;
  int val1;
  bool val2;
  void val3;
}

### Test declaring struct(structDecl) with one variable(structBody)
struct m {
  void t;
}

### Test method with varDecls and Stmts(fnDecl, fnBody, varDeclList, stmtList)
### with parameters(formals, formalDecl, formalsList)
void test(int a, bool b) {
  
  ### Test multiple variable declarations in function body
  bool b;
  bool c;
  int d;
  int e;
  int f;
  
  ### Test boolean constant expressions
  b = false;
  c = true;

  ### Test integer constant expressions
  d = 124;
  e = 0;
  f = 123456789;

  ### String constant expressions
  g = "test";
  h = "\"esc_test\"";
  i = "";

  ### Parenthesis around expression
  j = (123);

  ### Method call w/ and w/o params
  k = method();
  l = method1(y);
  m = method2(true, false, x);

  ### Test dot access assignment and access
  obj.val = obj.val * 5 + 2;

  ### Test unary (not / minus)
  obj = !obj;
  obj = -obj + 4;

  ### Test relational operators
  val = (val > val1) || (val2 < val3);
  val1 = (val >= val1) || (val2 <= val3);
  val2 = (val == val2) || (val2 != val3);

  ### Test OR and AND
  val = (val || val1) && (val2 || val3);
  val = (val && val1) || (val2 && val3);

  ### Test plus, minus, divide, times
  val = (val + val1) / (val2 * (val3 - val4));
  val = (val * val1) - (val2 + (val3 / val4));

  ### Test assignment exp
  val = val || (val2 = val3);

  ### Test all statements

  a = b;
  b.val = c;

  ++a;
  ++b.val;

  --a;
  --b.val;

  receive >> a;
  receive >> b.val;

  print << a;
  print << b.val;

  if (a == b + c.val - 5 * 2) {
    int a;
    c = d;
  }

  if (c != d || a > b) {
    g = h;
  } else {
    f = f * 2;
  }
  
  if ("true") {
  }

  while (!b) {
    bool f;
    int var;
    c = d * 3;
    var = 10;
  }
  
  while ("working") {
  }

  repeat (d * 9 / 2) {
    int var;
    bool s;
    f.val = 4 * test();
    b = true;
  }
  
  repeat ("repeat") {
  }

  ret 4;
  ret;

  test();
  test(false, 2, 3);
}

### Test empty method declaration(fnDecl, fnBody, varDeclList, stmtList) 
### with 1 parameter(formals, formalDecl, formalsList)
bool empty_method(bool a) {
}

### Test method declaration(fnDecl, fnBody, varDeclList, stmtList)
### without parameters(formals)
int main() {

  int a;
  bool b;
  void c;

  if (b == -1) {
    x = 4 + 3 * 5 - y;
    while (c) {
      y = y * 2 + x;
    }
  } else {
    x = 0;
  }
}
