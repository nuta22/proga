#define _CRT_SECURE_NO_WARNINGS
#include "input.h"
#include <stdio.h>

int input_int(int min, int max) {
    int result;
retry:
    while (scanf("%d", &result) != 1) {
        printf("�������� ����. ���������: ");
        char c;
        while ((c = getchar()) != EOF && c != '\n');
    }
    if (min <= result && result <= max) {
        return result;
    }
    printf("����� �� ������ � �������� [%d, %d]. ���������: ", min, max);
    goto retry;
}

void input_string(char* buf, int count) {
    char format[16];
    sprintf(format, "%%%ds", count - 1);
    scanf(format, buf);
}

date input_date() {
    date result = {};


    printf("������� ���: ");    result.year = input_int(1970, 9999);
    printf("������� �����: ");  result.month = input_int(1, 12);
    printf("������� ����: ");   result.day = input_int(1, 31);
    printf("������� ���: ");    result.hour = input_int(0, 24);
    printf("������� ������: "); result.minute = input_int(0, 60);

    return result;
}

endpoint input_endpoint() {
    endpoint result = {};
    printf("������� ����:\n");     result.date = input_date();
    printf("������� �������� ������: "); input_string(result.name, sizeof(result.name));
    return result;
}

entry input_entry() {
    entry result = {};
    printf("������� �������� ����������� ������:\n"); result.start = input_endpoint();
    printf("������� ���������:\n");      result.cost = input_int(1, INT32_MAX);
    return result;
}