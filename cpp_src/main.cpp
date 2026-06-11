#include <iostream>
#include <cassert>
#include "src/MyBigNumber.h"

void testEqualLength() {
    MyBigNumber bn;
    assert(bn.sum("123", "456") == "579");
    cout << "[PASS] testEqualLength: 123 + 456 = 579" << endl;
}

void testUnequalLength() {
    MyBigNumber bn;
    assert(bn.sum("1234", "897") == "2131");
    cout << "[PASS] testUnequalLength: 1234 + 897 = 2131" << endl;
}

void testContinuousCarry() {
    MyBigNumber bn;
    assert(bn.sum("999", "1") == "1000");
    cout << "[PASS] testContinuousCarry: 999 + 1 = 1000" << endl;
}

void testAddWithZero() {
    MyBigNumber bn;
    assert(bn.sum("12345", "0") == "12345");
    assert(bn.sum("0", "6789") == "6789");
    cout << "[PASS] testAddWithZero: 12345 + 0, 0 + 6789" << endl;
}

void testBothZero() {
    MyBigNumber bn;
    assert(bn.sum("0", "0") == "0");
    cout << "[PASS] testBothZero: 0 + 0 = 0" << endl;
}

void testVeryLargeNumbers() {
    MyBigNumber bn;
    assert(bn.sum("99999999999999999999", "1") == "100000000000000000000");
    cout << "[PASS] testVeryLargeNumbers: 99999999999999999999 + 1" << endl;
}

void testSameNumber() {
    MyBigNumber bn;
    assert(bn.sum("5000", "5000") == "10000");
    cout << "[PASS] testSameNumber: 5000 + 5000 = 10000" << endl;
}

int main(int argc, char* argv[]) {
    // CLI mode: called from Spring Boot via ProcessBuilder
    if (argc == 3) {
        MyBigNumber bn;
        string result = bn.sum(argv[1], argv[2]);
        cout << "Result: " << result << endl;
        return 0;
    }

    // Invalid arguments
    if (argc != 1) {
        cerr << "Usage: ./mybignumber_core <num1> <num2>" << endl;
        cerr << "  Run without arguments to execute unit tests." << endl;
        return 1;
    }

    // Unit test mode: run all tests
    cout << "=== MyBigNumber Unit Tests ===" << endl;

    testEqualLength();
    testUnequalLength();
    testContinuousCarry();
    testAddWithZero();
    testBothZero();
    testVeryLargeNumbers();
    testSameNumber();

    cout << "\nAll tests passed!" << endl;
    cout << "Xem file history.log de kiem tra chi tiet cac buoc tinh toan." << endl;

    return 0;
}
