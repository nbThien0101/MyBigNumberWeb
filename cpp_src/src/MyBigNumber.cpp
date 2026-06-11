#include "MyBigNumber.h"
#include <fstream>
#include <algorithm>

string MyBigNumber::sum(string stn1, string stn2) {
    ofstream logFile("history.log", ios::app);

    logFile << "=== Phep toan: " << stn1 << " + " << stn2 << " ===" << endl;

    string result;
    int carry = 0;
    int i = static_cast<int>(stn1.size()) - 1;
    int j = static_cast<int>(stn2.size()) - 1;
    int step = 1;

    while (i >= 0 || j >= 0 || carry > 0) {
        int digit1 = (i >= 0) ? (stn1[i] - '0') : 0;
        int digit2 = (j >= 0) ? (stn2[j] - '0') : 0;
        int total = digit1 + digit2;

        logFile << "Buoc " << step << ": ";

        if (i >= 0 && j >= 0) {
            logFile << "Lay " << digit1 << " cong voi " << digit2 << " duoc " << total << ".";
        } else if (i >= 0) {
            logFile << "Lay " << digit1 << " (khong con so tuong ung o so thu 2).";
        } else if (j >= 0) {
            logFile << "Lay " << digit2 << " (khong con so tuong ung o so thu 1).";
        } else {
            logFile << "Xu ly so nho con lai.";
        }

        if (carry > 0) {
            logFile << " Cong tiep voi nho " << carry << " duoc " << (total + carry) << ".";
        }

        total += carry;
        carry = total / 10;
        int digit = total % 10;

        result += static_cast<char>(digit + '0');

        logFile << " Luu " << digit << " vao ket qua";
        if (!result.empty()) {
            string temp = result;
            reverse(temp.begin(), temp.end());
            logFile << " (Ket qua tam thoi: \"" << temp << "\")";
        }
        logFile << ".";

        if (carry > 0) {
            logFile << " Ghi nho " << carry << ".";
        }

        logFile << endl;

        --i;
        --j;
        ++step;
    }

    reverse(result.begin(), result.end());

    logFile << "Ket qua cuoi cung: " << result << endl;
    logFile << "---------------------------------------------------" << endl;

    logFile.close();

    return result;
}
