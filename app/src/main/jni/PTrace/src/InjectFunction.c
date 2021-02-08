/**********************************************************
  FileName: InjectFunction.c
  Description: 被注入模块函数
***********************************************************/
#include <stdio.h>
#include <stdlib.h>
#include <PrintLog.h>

int Inject_entry()
{
    LOGD("Inject_entry Func is called\n");
    return 0;
}