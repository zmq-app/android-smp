/**********************************************************
  FileName: InjectModule.c
  Description: ptrace注入到指定进程
  Url: https://xz.aliyun.com/t/5361
***********************************************************/
#include <stdio.h>
#include <stdlib.h>
#include <sys/user.h>
#include <asm/ptrace.h>
#include <sys/ptrace.h>
#include <sys/wait.h>
#include <sys/mman.h>
#include <dlfcn.h>
#include <dirent.h>
#include <unistd.h>
#include <string.h>
#include <elf.h>
#include <ptraceInject.h>
#include <PrintLog.h>

/*************************************************
  Description:    通过进程名称定位到进程的PID
  Input:          process_name为要定位的进程名称
  Output:         无
  Return:         返回定位到的进程PID,若为-1,表示定位失败
  Others:         无
*************************************************/
pid_t FindPidByProcessName(const char *process_name)
{
    int ProcessDirID = 0;
    pid_t pid = -1;
    FILE *fp = NULL;
    char filename[MAX_PATH] = {0};
    char cmdline[MAX_PATH] = {0};

    struct dirent * entry = NULL;

    if ( process_name == NULL )
        return -1;

    DIR* dir = opendir( "/proc" );
    if ( dir == NULL )
        return -1;

    while( (entry = readdir(dir)) != NULL )
    {
        ProcessDirID = atoi( entry->d_name );
        if ( ProcessDirID != 0 )
        {
            snprintf(filename, MAX_PATH, "/proc/%d/cmdline", ProcessDirID);
            fp = fopen( filename, "r" );
            if ( fp )
            {
                fgets(cmdline, sizeof(cmdline), fp);
                fclose(fp);

                if (strncmp(process_name, cmdline, strlen(process_name)) == 0)
                {
                    pid = ProcessDirID;
                    break;
                }
            }
        }
    }

    closedir(dir);
    return pid;
}

int main(int argc, char *argv[]) {
    char InjectModuleName[MAX_PATH] = "/data/libInjectFunction.so";  //注入模块全路径
    char RemoteCallFunc[MAX_PATH] = "Inject_entry";  //注入模块后调用模块函数名称
    //char InjectProcessName[MAX_PATH] = "com.itheima.smp";  //注入进程名称
    //char InjectProcessName[MAX_PATH] = "com.icbc.esx";
    char InjectProcessName[MAX_PATH] = "com.hw.cloudtestdemo";

    // 当前设备环境判断
#if defined(__i386__)
    LOGD("Current Environment x86");
    return -1;
#elif defined(__arm__)
    LOGD("Current Environment ARM");
#else
    LOGD("other Environment");
    return -1;
#endif

    pid_t pid = FindPidByProcessName(InjectProcessName);
    if (pid == -1)
    {
        printf("Get Pid Failed");
        return -1;
    }

    printf("begin inject process, RemoteProcess pid:%d, InjectModuleName:%s, RemoteCallFunc:%s\n", pid, InjectModuleName, RemoteCallFunc);
    int iRet = inject_remote_process(pid,  InjectModuleName, RemoteCallFunc,  NULL, 0);
    //int iRet = inject_remote_process_shellcode(pid,  InjectModuleName, RemoteCallFunc,  NULL, 0);

    if (iRet == 0)
    {
        printf("Inject Success\n");
    }
    else
    {
        printf("Inject Failed\n");
    }
    printf("end inject,%d\n", pid);
    return 0;
}

/**
 * 山东大数据项目--ptrace注入失败的Log
 * 2020-12-11 15:57:33.207 3330-3330/? D/INJECT: Current Environment ARM
 * 2020-12-11 15:57:33.212 3330-3330/? D/INJECT: attach process pid:3121
 * 2020-12-11 15:57:33.212 3330-3330/? D/INJECT: ARM_r0:0xfffffffc, ARM_r1:0xf06237c8, ARM_r2:0x10, ARM_r3:0xffffffff, ARM_r4:0x0,           ARM_r5:0x8, ARM_r6:0x733b85cc, ARM_r7:0x7ad8a, ARM_r8:0x16, ARM_r9:0x8620c888,           ARM_r10:0xe, ARM_ip:0x8620c878, ARM_sp:0x0, ARM_lr:0x38bcc9f3, ARM_pc:0x87bf9b80
 * 2020-12-11 15:57:33.217 3330-3330/? D/INJECT: mmap RemoteFuncAddr:0x20dcd
 * 2020-12-11 15:57:33.217 3330-3330/? D/INJECT: Write Remote Memory error, MemoryAddr:0xfffffff8
 * 2020-12-11 15:57:33.217 3330-3330/? D/INJECT: Call Remote mmap Func Failed
 * 2020-12-11 15:57:33.217 3330-3330/? D/INJECT: detach process pid:3121
 */