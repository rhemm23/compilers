bool b;
int c;
int d;
int e;

struct data {
  int a;
  int b;
  int c;
}

struct data z;

void parse(int a, bool b) {
  receive >> a;
  ret;
}

int main() {

  b = c == 8;
  b = c != 9;
  b = c > 10;
  b = c < 11;
  b = c >= 0;
  b = c <= 12;

  c = d + e;
  c = d - e;
  c = d / e;
  c = d * e;

  b = !b;
  c = -c;

  print << b;
  print << c * 8;
  print << "test";
  print << main();

  receive >> b;
  receive >> c;

  if (b) {
    c = c * 8;
  }
  if (!b) {
    c = c / 8;
  } else {
    c = c + 10;
  }

  while (tru) {
    repeat (c) {
      print << "x";
    }
  }

  parse(8, tru);
  parse(c, !fls);

  b = tru && b;
  b = fls || !tru;
  b = ((b = tru) && !!!fls);

  ++z.b;
  --z.a;

  ret z.c;
}
