/*
 * gc-openchat
 * Copyright (C) 2022  jie65535
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.github.jie65535.openchat.utils;

import java.util.HashMap;
import java.util.Map;

public class SensitiveWordFilter {

    // 定义一个内部类表示图中的节点
    private static class Node {
        // 用一个哈希表存储该节点的后继节点
        private final Map<Character, Node> children = new HashMap<>();
        // 用一个布尔值表示该节点是否是某个敏感词的结尾
        private boolean isEnd = false;
    }

    // 定义一个根节点
    private final Node root = new Node();

    // 定义一个添加敏感词的方法
    public void addWord(String word) {
        if (word == null || word.isEmpty()) {
            return;
        }
        // 从根节点开始遍历
        Node current = root;
        for (char c : word.toCharArray()) {
            // 如果当前节点没有以c为键的后继节点，就创建一个新的节点并添加到哈希表中
            if (!current.children.containsKey(c)) {
                current.children.put(c, new Node());
            }
            // 更新当前节点为后继节点
            current = current.children.get(c);
        }
        // 标记当前节点为某个敏感词的结尾
        current.isEnd = true;
    }

    // 定义一个检测聊天消息是否包含敏感词的方法
    public boolean isSensitive(String message) {
        if (message == null || message.isEmpty()) {
            return false;
        }
        // 遍历聊天消息中的每个字符作为起始位置
        for (int i = 0; i < message.length(); i++) {
            // 从根节点开始遍历
            Node current = root;
            for (int j = i; j < message.length(); j++) {
                char c = message.charAt(j);
                // 如果当前节点没有以c为键的后继节点，说明没有匹配到敏感词，跳出循环
                if (!current.children.containsKey(c)) {
                    break;
                }
                // 更新当前节点为后继节点
                current = current.children.get(c);
                // 如果当前节点是某个敏感词的结尾，说明匹配到了敏感词，返回true
                if (current.isEnd) {
                    return true;
                }
            }
        }
        // 遍历完聊天消息没有匹配到任何敏感词，返回false
        return false;
    }
}
