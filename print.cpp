#include "print.h"
#include <stdio.h>

char const* month_string(uint32_t month) {
    switch (month) {
    case 1: return "������";
    case 2: return "�������";
    case 3: return "�����";
    case 4: return "������";
    case 5: return "���";
    case 6: return "����";
    case 7: return "����";
    case 8: return "�������";
    case 9: return "��������";
    case 10: return "�������";
    case 11: return "������";
    case 12: return "�������";
    }
    return "";
}

void print(date date) {
    printf("%d:%d %d %s %d", date.hour, date.minute, date.day, month_string(date.month), date.year);
}

void print(endpoint endpoint) {
    printf("%s", endpoint.name);
    print(endpoint.date);
}

void print(entry entry) {
    printf("%s ", entry.start.name);
    print(entry.start.date);
    printf("\n���������: %d\n\n", entry.cost);
}