/*
 * Licensed to Elasticsearch under one or more contributor
 * license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright
 * ownership. Elasticsearch licenses this file to you under
 * the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.elasticsearch.analysis.common;

import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.ar.ArabicNormalizationFilter;
import org.apache.lucene.analysis.ar.ArabicStemFilter;
import org.apache.lucene.analysis.br.BrazilianStemFilter;
import org.apache.lucene.analysis.cjk.CJKBigramFilter;
import org.apache.lucene.analysis.cjk.CJKWidthFilter;
import org.apache.lucene.analysis.ckb.SoraniNormalizationFilter;
import org.apache.lucene.analysis.commongrams.CommonGramsFilter;
import org.apache.lucene.analysis.core.DecimalDigitFilter;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.analysis.core.UpperCaseFilter;
import org.apache.lucene.analysis.cz.CzechStemFilter;
import org.apache.lucene.analysis.de.GermanNormalizationFilter;
import org.apache.lucene.analysis.de.GermanStemFilter;
import org.apache.lucene.analysis.en.KStemFilter;
import org.apache.lucene.analysis.en.PorterStemFilter;
import org.apache.lucene.analysis.fa.PersianNormalizationFilter;
import org.apache.lucene.analysis.fr.FrenchAnalyzer;
import org.apache.lucene.analysis.hi.HindiNormalizationFilter;
import org.apache.lucene.analysis.in.IndicNormalizationFilter;
import org.apache.lucene.analysis.miscellaneous.ASCIIFoldingFilter;
import org.apache.lucene.analysis.miscellaneous.KeywordRepeatFilter;
import org.apache.lucene.analysis.miscellaneous.LengthFilter;
import org.apache.lucene.analysis.miscellaneous.LimitTokenCountFilter;
import org.apache.lucene.analysis.miscellaneous.ScandinavianFoldingFilter;
import org.apache.lucene.analysis.miscellaneous.ScandinavianNormalizationFilter;
import org.apache.lucene.analysis.miscellaneous.TrimFilter;
import org.apache.lucene.analysis.miscellaneous.TruncateTokenFilter;
import org.apache.lucene.analysis.miscellaneous.UniqueTokenFilter;
import org.apache.lucene.analysis.miscellaneous.WordDelimiterFilter;
import org.apache.lucene.analysis.miscellaneous.WordDelimiterGraphFilter;
import org.apache.lucene.analysis.ngram.EdgeNGramTokenFilter;
import org.apache.lucene.analysis.ngram.NGramTokenFilter;
import org.apache.lucene.analysis.payloads.DelimitedPayloadTokenFilter;
import org.apache.lucene.analysis.payloads.TypeAsPayloadTokenFilter;
import org.apache.lucene.analysis.reverse.ReverseStringFilter;
import org.apache.lucene.analysis.shingle.ShingleFilter;
import org.apache.lucene.analysis.snowball.SnowballFilter;
import org.apache.lucene.analysis.standard.ClassicFilter;
import org.apache.lucene.analysis.tr.ApostropheFilter;
import org.apache.lucene.analysis.util.ElisionFilter;
import org.elasticsearch.index.analysis.CharFilterFactory;
import org.elasticsearch.index.analysis.DelimitedPayloadTokenFilterFactory;
import org.elasticsearch.index.analysis.HtmlStripCharFilterFactory;
import org.elasticsearch.index.analysis.LimitTokenCountFilterFactory;
import org.elasticsearch.index.analysis.PreConfiguredTokenFilter;
import org.elasticsearch.index.analysis.TokenFilterFactory;
import org.elasticsearch.indices.analysis.AnalysisModule.AnalysisProvider;
import org.elasticsearch.plugins.AnalysisPlugin;
import org.elasticsearch.plugins.Plugin;
import org.tartarus.snowball.ext.DutchStemmer;
import org.tartarus.snowball.ext.FrenchStemmer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static org.elasticsearch.plugins.AnalysisPlugin.requriesAnalysisSettings;

public class CommonAnalysisPlugin extends Plugin implements AnalysisPlugin {
    @Override
    public Map<String, AnalysisProvider<TokenFilterFactory>> getTokenFilters() {
        Map<String, AnalysisProvider<TokenFilterFactory>> filters = new TreeMap<>();
        filters.put("asciifolding", ASCIIFoldingTokenFilterFactory::new);
        filters.put("word_delimiter", WordDelimiterTokenFilterFactory::new);
        filters.put("word_delimiter_graph", WordDelimiterGraphTokenFilterFactory::new);
        return filters;
    }

    public Map<String, AnalysisProvider<CharFilterFactory>> getCharFilters() {
        Map<String, AnalysisProvider<CharFilterFactory>> filters = new TreeMap<>();
        filters.put("html_strip", HtmlStripCharFilterFactory::new);
        filters.put("pattern_replace", requriesAnalysisSettings(PatternReplaceCharFilterFactory::new));
        filters.put("mapping", requriesAnalysisSettings(MappingCharFilterFactory::new));
        return filters;
    }

    @Override
    public List<PreConfiguredTokenFilter> getPreConfiguredTokenFilters() {
        List<PreConfiguredTokenFilter> filters = new ArrayList<>();
        filters.add(PreConfiguredTokenFilter.singleton("apostrophe", false, ApostropheFilter::new));
        filters.add(PreConfiguredTokenFilter.singleton("arabic_normalization", true, ArabicNormalizationFilter::new));
        filters.add(PreConfiguredTokenFilter.singleton("arabic_stem", false, ArabicStemFilter::new));
        filters.add(PreConfiguredTokenFilter.singleton("asciifolding", true, ASCIIFoldingFilter::new));
        filters.add(PreConfiguredTokenFilter.singleton("brazilian_stem", false, BrazilianStemFilter::new));
        filters.add(PreConfiguredTokenFilter.singleton("cjk_bigram", false, CJKBigramFilter::new));
        filters.add(PreConfiguredTokenFilter.singleton("cjk_width", true, CJKWidthFilter::new));
        filters.add(PreConfiguredTokenFilter.singleton("classic", false, ClassicFilter::new));
        filters.add(PreConfiguredTokenFilter.singleton("common_grams", false,
                input -> new CommonGramsFilter(input, CharArraySet.EMPTY_SET)));
        filters.add(PreConfiguredTokenFilter.singleton("czech_stem", false, CzechStemFilter::new));
        filters.add(PreConfiguredTokenFilter.singleton("decimal_digit", true, DecimalDigitFilter::new));
        filters.add(PreConfiguredTokenFilter.singleton("delimited_payload_filter", false, input ->
                new DelimitedPayloadTokenFilter(input,
                        DelimitedPayloadTokenFilterFactory.DEFAULT_DELIMITER,
                        DelimitedPayloadTokenFilterFactory.DEFAULT_ENCODER)));
        filters.add(PreConfiguredTokenFilter.singleton("dutch_stem", false, input -> new SnowballFilter(input, new DutchStemmer())));
        filters.add(PreConfiguredTokenFilter.singleton("edge_ngram", false, input ->
                new EdgeNGramTokenFilter(input, EdgeNGramTokenFilter.DEFAULT_MIN_GRAM_SIZE, EdgeNGramTokenFilter.DEFAULT_MAX_GRAM_SIZE)));
        // TODO deprecate edgeNGram
        filters.add(PreConfiguredTokenFilter.singleton("edgeNGram", false, input ->
                new EdgeNGramTokenFilter(input, EdgeNGramTokenFilter.DEFAULT_MIN_GRAM_SIZE, EdgeNGramTokenFilter.DEFAULT_MAX_GRAM_SIZE)));
        filters.add(PreConfiguredTokenFilter.singleton("elision", true,
                input -> new ElisionFilter(input, FrenchAnalyzer.DEFAULT_ARTICLES)));
        filters.add(PreConfiguredTokenFilter.singleton("french_stem", false, input -> new SnowballFilter(input, new FrenchStemmer())));
        filters.add(PreConfiguredTokenFilter.singleton("german_normalization", true, GermanNormalizationFilter::new));
        filters.add(PreConfiguredTokenFilter.singleton("german_stem", false, GermanStemFilter::new));
        filters.add(PreConfiguredTokenFilter.singleton("hindi_normalization", true, HindiNormalizationFilter::new));
        filters.add(PreConfiguredTokenFilter.singleton("indic_normalization", true, IndicNormalizationFilter::new));
        filters.add(PreConfiguredTokenFilter.singleton("keyword_repeat", false, KeywordRepeatFilter::new));
        filters.add(PreConfiguredTokenFilter.singleton("kstem", false, KStemFilter::new));
        filters.add(PreConfiguredTokenFilter.singleton("length", false, input ->
                new LengthFilter(input, 0, Integer.MAX_VALUE)));  // TODO this one seems useless
        filters.add(PreConfiguredTokenFilter.singleton("limit", false, input ->
                new LimitTokenCountFilter(input,
                        LimitTokenCountFilterFactory.DEFAULT_MAX_TOKEN_COUNT,
                        LimitTokenCountFilterFactory.DEFAULT_CONSUME_ALL_TOKENS)));
        filters.add(PreConfiguredTokenFilter.singleton("ngram", false, NGramTokenFilter::new));
        // TODO deprecate nGram
        filters.add(PreConfiguredTokenFilter.singleton("nGram", false, NGramTokenFilter::new));
        filters.add(PreConfiguredTokenFilter.singleton("persian_normalization", true, PersianNormalizationFilter::new));
        filters.add(PreConfiguredTokenFilter.singleton("porter_stem", false, PorterStemFilter::new));
        filters.add(PreConfiguredTokenFilter.singleton("reverse", false, input -> new ReverseStringFilter(input)));
        filters.add(PreConfiguredTokenFilter.singleton("russian_stem", false, input -> new SnowballFilter(input, "Russian")));
        filters.add(PreConfiguredTokenFilter.singleton("scandinavian_folding", true, ScandinavianFoldingFilter::new));
        filters.add(PreConfiguredTokenFilter.singleton("scandinavian_normalization", true, ScandinavianNormalizationFilter::new));
        filters.add(PreConfiguredTokenFilter.singleton("shingle", false, ShingleFilter::new));
        filters.add(PreConfiguredTokenFilter.singleton("snowball", false, input -> new SnowballFilter(input, "English")));
        filters.add(PreConfiguredTokenFilter.singleton("sorani_normalization", true, SoraniNormalizationFilter::new));
        filters.add(PreConfiguredTokenFilter.singleton("stemmer", false, PorterStemFilter::new));
        // The stop filter is in lucene-core but the English stop words set is in lucene-analyzers-common
        filters.add(PreConfiguredTokenFilter.singleton("stop", false, input -> new StopFilter(input, StopAnalyzer.ENGLISH_STOP_WORDS_SET)));
        filters.add(PreConfiguredTokenFilter.singleton("trim", false, TrimFilter::new));
        filters.add(PreConfiguredTokenFilter.singleton("truncate", false, input -> new TruncateTokenFilter(input, 10)));
        filters.add(PreConfiguredTokenFilter.singleton("type_as_payload", false, TypeAsPayloadTokenFilter::new));
        filters.add(PreConfiguredTokenFilter.singleton("unique", false, input -> new UniqueTokenFilter(input)));
        filters.add(PreConfiguredTokenFilter.singleton("uppercase", true, UpperCaseFilter::new));
        filters.add(PreConfiguredTokenFilter.singleton("word_delimiter", false, input ->
                new WordDelimiterFilter(input,
                        WordDelimiterFilter.GENERATE_WORD_PARTS
                      | WordDelimiterFilter.GENERATE_NUMBER_PARTS
                      | WordDelimiterFilter.SPLIT_ON_CASE_CHANGE
                      | WordDelimiterFilter.SPLIT_ON_NUMERICS
                      | WordDelimiterFilter.STEM_ENGLISH_POSSESSIVE, null)));
        filters.add(PreConfiguredTokenFilter.singleton("word_delimiter_graph", false, input ->
                new WordDelimiterGraphFilter(input,
                        WordDelimiterGraphFilter.GENERATE_WORD_PARTS
                      | WordDelimiterGraphFilter.GENERATE_NUMBER_PARTS
                      | WordDelimiterGraphFilter.SPLIT_ON_CASE_CHANGE
                      | WordDelimiterGraphFilter.SPLIT_ON_NUMERICS
                      | WordDelimiterGraphFilter.STEM_ENGLISH_POSSESSIVE, null)));
        return filters;
    }
}
