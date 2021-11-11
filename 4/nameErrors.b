int a;
struct test_struct {
  int size;
};
void func() {}

// Multiply declared identifier

int mul_var;                     //
int mul_var;                     // Multiply declared identifier
bool mul_var;                    // Multiply declared identifier
int mul_var() {}                 // Multiply declared identifier
struct mul_var {                 // Multiply declared identifier
  int mul_var;                   //
};                               //
int mul_var() {}                 // Multiply declared identifier
int mul_func() {}                //
int mul_func() {}                // Multiply declared identifier
int mul_formal(int a, int a) {}  // Multiply declared identifier
struct mul_struct {              //
  int mul_var;                   //
  int mul_var;                   // Multiply declared identifier
  bool mul_var;                  // Multiply declared identifier
  void mul_var;  // Multiply declared identifier + Non-function declared void
};               //
struct mul_struct {  // Multiply declared identifier
  int mul_var;       //
  bool mul_var;      // Multiply declared identifier
};                   //
int mul_body() {     //
  int mul_var;       //
  int mul_var;       // Multiply declared identifier
  int a;             //
  int a;             // Multiply declared identifier
}

// Undeclared identifier

int test_undec() {
  // undeclared var
  a = undec_var;                      // Undeclared identifier
  undec_var = a;                      // Undeclared identifier
  undec_var = undec_var;              // Undeclared identifier * 2
  a = undec_var + a;                  // Undeclared identifier
  undec_var = undec_var - undec_var;  // Undeclared identifier * 3

  // undeclared func
  undec_func();                          // Undeclared identifier
  a = undec_func(a);                     // Undeclared identifier
  undec_func(undec_func(undec_func()));  // Undeclared identifier * 3
}

// Dot-access of non-struct type
int test_dot_access() {
  a = a.b;      // Dot-access of non-struct type
  a.b = a;      // Dot-access of non-struct type
  a = a.b.c.d;  // Dot-access of non-struct type * 3
  a = func.a;   // Dot-access of non-struct type
}

// Invalid struct field name
int test_struct_function() {
  struct test_struct struct_var;
  a = struct_var.not_a_field;  // Invalid struct field name
}

// Non-function declared void

void void_var;                    // Non-function declared void
void mul_var;                     // Non-function declared void
                                  // Multiply declared identifier
struct void_struct {              //
  void mul_var;                   // Non-function declared void
};                                //
int void_formal(void void_var) {  // Non-function declared void
  void a;                         // Non-function declared void
  void a;                         // Non-function declared void
                                  // Multiply declared identifier
}

// Invalid name of struct type

struct undec_struct bad_struct_var;  // Invalid name of struct type
struct undec_struct a;               // Invalid name of struct type
                                     // Multiply declared identifier