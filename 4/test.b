struct Point {
  int x;
  int y;
};
int f(int x, bool b) {}
void g() {
  int a;
  bool b;
  struct Point p;
  p.x = a;
  b = a == 3;
  f(a + p.y * 2, b);
  g();
}
int h(int h) {
  int h;
  h = 5;
  while (5 == 3) {
  }
  repeat(h == 3) {}
}
int main() {
  int a;
  if (a == 1) {
    int a;
    if (a == 1) {
      int a;
      int c;
      a = 4;
    } else {
      int a;
      int b;
      a = a;
    }
  }
  while (a == 1) {
    int b;
    while (b < a) {
      int a;
      int c;
      a = 1;
      while (c == 1) {
        cout << a;
      }
    }
  }
  cout << a;
}