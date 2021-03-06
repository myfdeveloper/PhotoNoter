package com.yydcdut.note.markdown;

import android.text.SpannableStringBuilder;

import com.yydcdut.note.markdown.chain.GrammarMultiChains;
import com.yydcdut.note.markdown.chain.GrammarSingleChain;
import com.yydcdut.note.markdown.chain.IResponsibilityChain;
import com.yydcdut.note.markdown.chain.MultiGrammarsChain;
import com.yydcdut.note.markdown.grammar.GrammarFactory;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by yuyidong on 16/5/3.
 */
public class MarkdownParser {
    IResponsibilityChain mChain = null;

    {
        mChain = new GrammarSingleChain(GrammarFactory.getGrammar(GrammarFactory.GRAMMAR_BLOCK_QUOTES));
        IResponsibilityChain orderListChain = new GrammarSingleChain(GrammarFactory.getGrammar(GrammarFactory.GRAMMAR_ORDER_LIST));
        IResponsibilityChain unOrderListChain = new GrammarSingleChain(GrammarFactory.getGrammar(GrammarFactory.GRAMMAR_UNORDER_LIST));
        IResponsibilityChain centerAlignChain = new GrammarMultiChains(GrammarFactory.getGrammar(GrammarFactory.GRAMMAR_CENTER_ALIGN));
        IResponsibilityChain headerLine3Chain = new GrammarMultiChains(GrammarFactory.getGrammar(GrammarFactory.GRAMMAR_HEADER_LINE_3));
        IResponsibilityChain headerLine2Chain = new GrammarMultiChains(GrammarFactory.getGrammar(GrammarFactory.GRAMMAR_HEADER_LINE_2));
        IResponsibilityChain headerLine1Chain = new GrammarMultiChains(GrammarFactory.getGrammar(GrammarFactory.GRAMMAR_HEADER_LINE_1));
        IResponsibilityChain multiChain = new MultiGrammarsChain(
                GrammarFactory.getGrammar(GrammarFactory.GRAMMAR_BOLD),
                GrammarFactory.getGrammar(GrammarFactory.GRAMMAR_ITALIC));
        mChain.setNextHandleGrammar(orderListChain);
        orderListChain.setNextHandleGrammar(unOrderListChain);
        unOrderListChain.setNextHandleGrammar(centerAlignChain);
        centerAlignChain.addNextHandleGrammar(headerLine3Chain);
        centerAlignChain.addNextHandleGrammar(multiChain);
        headerLine3Chain.addNextHandleGrammar(headerLine2Chain);
        headerLine3Chain.addNextHandleGrammar(multiChain);
        headerLine2Chain.addNextHandleGrammar(headerLine1Chain);
        headerLine2Chain.addNextHandleGrammar(multiChain);
        headerLine1Chain.addNextHandleGrammar(multiChain);
    }

    @Inject
    @Singleton
    public MarkdownParser() {
    }

    public SpannableStringBuilder parse(String content) {
        String[] lines = content.split("\n");
        SpannableStringBuilder ssb = new SpannableStringBuilder();
        for (String line : lines) {
            SpannableStringBuilder lineSSB = new SpannableStringBuilder(line);
            mChain.handleGrammar(lineSSB);
            ssb.append(lineSSB);
            ssb.append("\n");
        }
        return ssb;
    }

}
