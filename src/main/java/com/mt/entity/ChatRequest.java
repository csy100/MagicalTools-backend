package com.mt.entity;

import cn.hutool.setting.Setting;
import lombok.Data;

/**
 * @author : wzy
 * @since : 2023/8/10 19:26
 * <p>
 *
 * <p>
 * Created in IDEA
 */
@Data
public class ChatRequest {
    // 聊天对象
    private Chat chat;

    // 聊天设置
    private Setting setting;

    @Data
    public static class Setting {
        // 系统扮演角色，即预设
        private String rolePlay;

        /**
         * 模型名称
         */
        private String model;

        /**
         * 使用什么取样温度，0到2之间。越高越奔放。越低越保守。
         * <p>
         * 不要同时改这个和topP
         */
        private Double temperature;

        /**
         * 最大上下文长度
         */
        private int n;

        /**
         * 最大使用token数
         */
        private int maxTokens;

        /**
         * 介于 -2.0 和 2.0 之间的数字。
         * <p>
         * 正值会根据新标记到目前为止在文本中的现有频率来惩罚新标记
         * <p>
         * 从而降低模型逐字重复同一行的可能性
         * <p>
         * 举例：你对人工智能的发展有什么看法？
         * （1）高主题重复度回答：人工智能的发展是一个非常重要的话题，它将会改变我们的生活和工作方式。
         * 在未来，我们将会看到越来越多的智能机器和机器人出现，他们将会代替人类完成一些重复性和危险性的工作。
         * 此外，人工智能还将会带来很多挑战和机会，我们需要认真思考如何让人工智能服务于人类的利益。
         * （2）低主题重复度回答：我认为人工智能是一项非常有前途的技术，它可以帮助我们解决许多实际问题。
         * 例如，在医疗领域，人工智能可以帮助医生更快速、准确地诊断病情；在交通领域，人工智能可以帮助我们更安全地驾驶。
         * 但是，人工智能也存在一些潜在的风险和问题，比如失业和隐私泄露等问题，我们需要认真评估和解决这些问题。
         */
        private Double presencePenalty;

        /**
         * 介于 -2.0 和 2.0 之间的数字。
         * <p>
         * 正值会根据新标记到目前为止是否出现在文本中来惩罚它们
         * <p>
         * 从而增加模型讨论新主题的可能性
         * <p>
         * 如果值为0，则可能出现糟糕的回答:狗是一种"非常"可爱的动物，狗的毛发"非常"柔软，狗"非常"喜欢跑来跑去，狗的尾巴也"非常"有趣。 如果值为2,则可能回答:狗是一种"非常"可爱的动物，它的毛发"非常"柔软，喜欢在草地上奔跑，它的尾巴也"非常"有趣。
         */
        private Double frequencyPenalty;
    }
}
