struct x {
  int y;
  bool z;
}

struct h {
  int a;
  int b;
  struct x c;
}

void test(int a, bool b) {
  struct x c;
  receive >> c.y;
}

int main() {
  struct h a;
  print << (a.c.y == 2);
  test(3 + 4*a.a, 34 == (a.b * 2 + 3));
}
