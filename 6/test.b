
int g;

void test() {
  ++g;
  ret;
  print << "Failed\n";
}

int sub(int a, int b) {
  ret a - b;
}

int div(int a, int b) {
  ret a / b;
}

int mult(int a, int b) {
  ret a * b;
}

int add(int a, int b) {
  ret a + b;
}

void main() {
  int a;
  g = 0;
  a = 10;
  --a;
  ++a;
  if (a == 5) {
    print << "Failed test";
  }
  if (a == 10) {
    while (a > 0) {
      --a;
      if (a == 5) {
        print << "a == 5\n";
      } else {
        print << "a != 5\n";
      }
    }
  }
  if (tru && !fls) {
    print << mult(3, 5) + add(1, 2) + div(3, 1) - (-sub(8, 6));
    print << "\n";
  }
  if (fls && tru) {
    print << "Failed\n";
  } else {
    print << "Short circuit AND passed\n";
  }
  if (!fls || fls) {
    print << "Short circuit OR and NOT working\n";
  } else {
    print << "Failed\n";
  }
  if (((a = 8) > 7) && 7 < 8 && 8 >= 8 && 7 <= 7 && (fls != tru) && (tru == tru)) {
    print << "Comparison operators working\n";
  } else {
    print << "Failed\n";
  }
  test();
  test();
  if (g != 2) {
    print << "Failed\n";
  }
  print << "Enter a number: ";
  receive >> g;
  print << "You entered: ";
  print << g;
  print << "\n";
  print << "SUCCESS";
}
