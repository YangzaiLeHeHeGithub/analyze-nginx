package com.mobile.analyzenone.analyze;

import com.mobile.analyzenone.bean.AnalyzeDataVo;
import com.mobile.analyzenone.util.AnalyzeUtil;
import com.mobile.analyzenone.util.ExcelReader;
import com.mobile.analyzenone.util.ExcelWriter;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.text.NumberFormat;
import java.util.*;

/**
 * AnalyzeNginx
 *
 * @author xuyang
 * @version 1.0
 * @description 分析Nginx日志主类
 * @date 2020/8/5 15:18
 */
@Slf4j
@Component
@Order(value = 1)
public class StartAnalyze implements ApplicationRunner {


    /**
     * 关闭app
     */
    @Autowired
    private ConfigurableApplicationContext context;


    @Value("${analyze.interface-excel-dir:}")
    private String interfaceExcelDir;
    @Value("${analyze.nginx-log-dir:}")
    private String nginxLogDir;
    @Value("${analyze.result-dir:}")
    private String resultDir;

    private static String ENCODING = "UTF-8";

    private static Double staticCount = 0D;

    private static Double totalCount = 0D;

    private static Double apiCount = 0D;

    private static Double notCount = 0D;

    private static Double emptyCount = 0D;

    private static Map<String, Double> exist = new HashMap<>();

    private static Map<String, Double> unknown = new HashMap<>();

    private static String tempString = "";

    @Override
    public void run(ApplicationArguments args) throws Exception {
        System.out.println("等待分析的接口存放的位置："+ interfaceExcelDir);
        System.out.println("前端的Nginx存放的位置："+ nginxLogDir);
        System.out.println("分析结果存放的位置： "+ resultDir);

        Set<String> interfaceSet = ExcelReader.readExcel(interfaceExcelDir);
        //读取日志
        readTxtFile(nginxLogDir, interfaceSet);
        //打印最终信息
        printMessage();
        // 有效百分比分析
        List<AnalyzeDataVo> resultList = resultAnalyze(exist);
        // 排序
        resultList.sort((y, x) -> Double.compare(x.getCount(), y.getCount()));

        ExcelWriter.writeExcel(resultList, resultDir);
        context.close();


    }


    /**
     * 分析结果并包装数据
     * @param result
     * @return
     */
    public static List<AnalyzeDataVo> resultAnalyze(Map<String, Double> result) {
        if (CollectionUtils.isEmpty(result)) {
            return Lists.newArrayList();
        }
        List<AnalyzeDataVo> resultList = new ArrayList<>();
        result.entrySet().stream().forEach(map ->{
            AnalyzeDataVo dataVo = new AnalyzeDataVo();
            String[] info = map.getKey().split(" ");
            Double count = map.getValue();
            dataVo.setMethod(info[0]);
            dataVo.setApi(info[1]);
            dataVo.setDesc(info[2]);
            dataVo.setCount(count);
            // 计算百分比
            dataVo.setPercent(AnalyzeUtil.getPercent(count, apiCount));
            resultList.add(dataVo);
        });
        return resultList;
    }

    /**
     * 功能：Java读取txt文件的内容
     * 步骤：1：先获得文件句柄
     * 2：获得文件句柄当做是输入一个字节码流，需要对这个输入流进行读取
     * 3：读取到输入流后，需要读取生成字节流
     * 4：一行一行的输出。readline()。
     * 备注：需要考虑的是异常情况
     *
     * @param filePath
     */
    public static void readTxtFile(String filePath, Set<String> set) {
        //暂存文本内容
        String lineTxt = null;
        //需要统计的集合大小
        int setSize = set.size();
        File file = new File(filePath);
        //判断文件是否存在
        if (file.isFile() && file.exists()) {
            //根据编码格式获取文件流
            try (InputStreamReader read = new InputStreamReader(new FileInputStream(file), ENCODING); BufferedReader bufferedReader = new BufferedReader(read)) {

                while ((lineTxt = bufferedReader.readLine()) != null) {
                    //为空继续循环
                    if (StringUtils.isBlank(lineTxt)) {
                        continue;
                    }
                    totalCount++;
                    System.out.println("读取第[ " + totalCount + " ]行");
                    String line = AnalyzeUtil.findRequestText(lineTxt);
                    if (Objects.equals(line, "")) {
                        emptyCount++;
                        continue;
                    }
                    if (line.contains("static")) {
                        staticCount++;
                        continue;
                    }
                    //是否有匹配标志位
                    Boolean hasMatch = false;
                    //循环次数
                    int cyclicCount = 0;
                    setLineStatus(line, false);
                    for (String item : set) {
                        cyclicCount++;
                        // 增加line判断状态
                        if (checkLineStatus(line)) {
                            continue;
                        }
                        // 判断前缀
                        if (AnalyzeUtil.checkMethod(item, line)) {
                            // 有path参数
                            if (item.indexOf("{") != -1) {
                                if (AnalyzeUtil.checkURL(item, line)) {
                                    if (AnalyzeUtil.matchHander(item, line)) {
                                        statistics(item);
                                        apiCount++;
                                        hasMatch = true;
                                        setLineStatus(line, true);
                                        continue;
                                    }
                                } else {
                                    // 无path参数 , 直接对比
                                    if (AnalyzeUtil.checkNotPathLine(item, line)) {
                                        statistics(item);
                                        apiCount++;
                                        hasMatch = true;
                                        setLineStatus(line, true);
                                        continue;
                                    }
                                }
                            }
                        }
                        //经过一顿操作还是没有匹配的 并且已经循环玩分析的Set，那么就计数吧
                        if (!hasMatch && cyclicCount == setSize) {
                            notCount++;
                            System.out.println("line text = " + AnalyzeUtil.analyzeLine(lineTxt));
                        }

                    };

                }

            } catch (Exception e) {
                System.out.println("读取文件内容出错");
                System.out.println("line=" + lineTxt);
                log.error("读取文件内容出错" ,e);
            }

        }


    }
    private static void printMessage(){
        System.out.println("总请求数：" + totalCount);
        System.out.println("静态资源请求：" + staticCount);
        System.out.println("API请求：" + apiCount);
        System.out.println("未知请求：" + notCount);
        // 存在的
        for (Map.Entry<String, Double> map : exist.entrySet()) {
            System.out.println("exist==" + map.getKey() + " " + map.getValue());
        }
        // 不存在的
        for (Map.Entry<String, Double> map : unknown.entrySet()) {
            System.out.println("unknown" + map.getKey() + " " + map.getValue());
        }
    }
    /**
     * 记录统计数据
     * @param key
     */
    public static void statistics(String key) {
        if (exist.containsKey(key)) {
            Double count = exist.get(key) + 1;
            exist.put(key, count);
        } else {
            exist.put(key, 1D);
        }
    }
    /**
     * 判断line状态
     * @param line
     * @return
     */
    public static Boolean checkLineStatus(String line) {
        String[] temp = tempString.split("@@");
        if (Objects.equals("true",temp[1])) {
            return true;
        }
        return false;
    }
    /**
     * 设置line状态
     * @param line
     * @param status
     */
    private static void setLineStatus(String line, Boolean status) {
        if (status) {
            tempString = line + "@@true";
        } else {
            tempString = line + "@@false";
        }

    }
}
 