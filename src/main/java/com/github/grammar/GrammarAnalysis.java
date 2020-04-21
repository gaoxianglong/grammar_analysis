/*
 * Copyright 2019-2119 gao_xianglong@sina.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.grammar;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

/**
 * 递归深度优先CFG文法推导
 *
 * @author gao_xianglong@sina.com
 * @version 0.1-SNAPSHOT
 * @date created in 2020/4/16 6:28 下午
 */
public class GrammarAnalysis {
    /**
     * 推导结束标记
     */
    private boolean flag = false;
    /**
     * 用于存储每一个非终结符对应的产生式体
     * 比如:S->S+T|S-T|T
     */
    private Map<String, LinkedList<String>> productionMap;
    /**
     * 非终结符集合
     */
    private List<String> non_terminals;
    /**
     * 目标表达式
     */
    private String expression;
    /**
     * 存储文法推导过程集合
     */
    private LinkedList<String> analysis;
    /**
     * 二元运算文法
     */
    private final String GRAMMARS[] = {"S -> S + T | S - T | T",
            "T -> T * F | T / F | F",
            "F -> id"};

    GrammarAnalysis() {
        productionMap = new ConcurrentHashMap<>();
        non_terminals = new ArrayList<>();
        analysis = new LinkedList<>();
    }

    /**
     * 相关初始化操作
     *
     * @return
     */
    GrammarAnalysis init() {
        Stream.of(GRAMMARS).forEach(x -> {
            //x = x.replaceAll("\\s*", "");
            String[] temp = x.split("->");
            if (temp.length > 1) {
                LinkedList body = new LinkedList<>();
                Stream.of(temp[1].trim().split("\\|")).forEach(y -> body.add(y.trim()));
                productionMap.put(temp[0].trim(), body);
            }
        });
        if (!productionMap.isEmpty()) {
            System.out.println(String.format("Grammar:\n%s", productionMap));
            non_terminals.addAll(productionMap.keySet());
        }
        return this;
    }

    GrammarAnalysis parse() {
        if (!analysis.isEmpty()) {
            analysis.clear();
        }
        try {
            parse("S");//从文法开始符号开始
        } finally {
            flag = false;
        }
        return this;
    }

    /**
     * 展开文法推导,基于DFS递归深度优先算法
     *
     * @param production
     */
    void parse(String production) {
        Objects.requireNonNull(production);
        if (!analysis.isEmpty()) {
            //验证推导式和表达式格式是否一致，以此降低递归深度
            if (isMatching(production, expression)) return;
        }
        if (production.equals(expression)) {//表达式与推导式等价时代表推导完成
            flag = true;
            return;
        }
        //最左推导，比如:id+id, E => E + E => id + E => id + id
        for (var i = 0; i < production.length(); i++) {
            var non_terminal = String.valueOf(production.charAt(i));//从左到右依次获取终结符
            if (!non_terminals.contains(non_terminal)) continue;
            for (var body : productionMap.get(non_terminal)) {//获取对应的相关产生式体
                var temp = production.replaceFirst(non_terminal, body);//将非终结符依次替换为产生式体
                analysis.add(String.format("%s\t=>\t%s", production, temp));
                parse(temp);
                if (flag) return;
                analysis.removeLast();
            }
        }
    }

    /**
     * 验证推导式和表达式格式是否一致
     *
     * @param s1
     * @param s2
     * @return
     */
    boolean isMatching(String s1, String s2) {
        return getCharacter(s1) > getCharacter(s2);
    }

    /**
     * 获取产生式中终结符或非终结符个数
     *
     * @param str
     * @return
     */
    int getCharacter(String str) {
        Objects.requireNonNull(str);
        return str.split("\\s").length;
    }

    void print() {
        if (analysis.isEmpty()) {
            System.out.println(String.format("expression:%s, analysis failed!",
                    expression));
            return;
        }
        analysis.forEach(x -> System.out.println(String.format("\t%s", x)));
    }

    public static void main(String[] args) {
        var scan = new Scanner(System.in);
        var ga = new GrammarAnalysis().init();
        while (true) {
            System.out.print("\ninput expression:");
            var input = scan.nextLine();
            if (input.toLowerCase().equals("bye")) System.exit(0);
            System.out.println("analysis:");
            if (Objects.nonNull(input)) {
                ga.expression = input;
                ga.parse().print();
            }
        }
    }
}
