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
  ret;
  ret fls;
}

void main() {

  bool d;
  struct data c;
  struct data a;

  print << main;
  print << data;
  print << a;
  print << test();
  print << 2;

  receive >> main;
  receive >> data;
  receive >> a;

  a();
  test(1);
  test1(a.a + fls, 33, tru);

  ret a.b;
  ret fls > tru;
  ret fls < tru;
  ret fls <= tru;
  ret fls >= tru;

  a = fls + "";
  a = fls * "";
  a = fls / tru;
  a = tru - tru;

  d = (8 && fls);
  a = tru || "test";
  a = !"test";
  if (89) { }
  if(a) {
    print << "x";
  } else {
    print << "y";
  }

  while("test") { }
  repeat(fls) { }

  a = tru == 8;
  d = fls != "t";
  a = (d = 8);

  d = test() == test3();
  d = test == test3;
  d = data == data1;
  d = a == c;

  d = (test = test3);
  d = (data = data1);
  a = c;
}