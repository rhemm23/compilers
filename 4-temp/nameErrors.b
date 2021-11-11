struct point {
  int x;
  bool y;
}
struct hand {
  int fingers;
  bool palm;
  int palm;   // Multiply Decls
  bool thumb;
  int thumb;  // Multiply Decls
}
void f(int x, bool y) {
  int x;  // Multiply func params and body
  bool x; // Multiply func params and body
  int y;  // Multiply func params and body
  bool y; // Multiply func params and body
  z = 10; // use before declaration
  int z;
  struct point p;
  struct point p; /Multiply decl 
  pp.x;   // (Undeclared ID or dot access of non-struct type) bad Struct access
  p.z;    // Invalid struct field name
  void a; // bad declaration
  struct pointers b; // bad declaration
  x = p.x + c;  // undeclared var
  p.fingers = 10 + 5; // bad Struct access
  p.palm = x + y - 60; // bad Struct access
  void x; // bad declaration and Multiply var
  struct pointers x; // bad declaration and Multiply var
  int x (int y) {    // Multiply Decls
    int y = 10;      // Multiply Decls
  }
  if (i = 10) {  // Undeclared id
    int x;
    bool x; // Multiply decls
  }
  while (i = 10) {  // Undeclared id
    int x;
    bool x; // Multiply decls
  }
}
struct f {} // Multiply StructDecls
struct point {} // Multiply StructDecls
int f(void x, void y) { // Multiply func decl, bad decl 
  struct pointers y;    // bad declaration and Multiply var
}
int f(int x, int y) { // Multiply func decl
  struct point y;    // Multiply decl
}
struct hand { // Multiply struct decl
  int a;
  int a;      // shouldn't process
}
int f1 (int x, bool x, void x) {}  // Multiply Decls, Non-func decl void
