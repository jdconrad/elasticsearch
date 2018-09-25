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

package org.elasticsearch.painless;

import java.util.HashMap;
import java.util.Map;

/**
 * These tests run the Painless scripts used in the context docs against
 * slightly modified data designed around unit tests rather than a fully-
 * running Elasticsearch server.
 */
public class ContextExampleTests extends ScriptTestCase {

// *** SETUP ***

    // Docs Generator Code:

    /*

    import java.io.FileWriter;
    import java.io.IOException;

    public class Generator {

    public final static String[] theatres = new String[] {"Down Port", "Graye", "Skyline", "Courtyard"};
    public final static String[] plays = new String[] {"Driving", "Pick It Up", "Sway and Pull", "Harriot",
            "The Busline", "Ants Underground", "Exploria", "Line and Single", "Shafted", "Sunnyside Down",
            "Test Run", "Auntie Jo"};
    public final static String[] actors = new String[] {"James Holland", "Krissy Smith", "Joe Muir", "Ryan Earns",
            "Joel Madigan", "Jessica Brown", "Baz Knight", "Jo Hangum", "Rachel Grass", "Phoebe Miller", "Sarah Notch",
            "Brayden Green", "Joshua Iller", "Jon Hittle", "Rob Kettleman", "Laura Conrad", "Simon Hower", "Nora Blue",
            "Mike Candlestick", "Jacey Bell"};

    public static void writeSeat(FileWriter writer, int id, String theatre, String play, String[] actors,
                                 String date, String time, int row, int number, double cost, boolean sold) throws IOException {
        StringBuilder builder = new StringBuilder();
        builder.append("{ \"create\" : { \"_index\" : \"seats\", \"_type\" : \"seat\", \"_id\" : \"");
        builder.append(id);
        builder.append("\" } }\n");
        builder.append("{ \"theatre\" : \"");
        builder.append(theatre);
        builder.append("\", \"play\" : \"");
        builder.append(play);
        builder.append("\", \"actors\": [ \"");
        for (String actor : actors) {
            builder.append(actor);
            if (actor.equals(actors[actors.length - 1]) == false) {
                builder.append("\", \"");
            }
        }
        builder.append("\" ], \"date\": \"");
        builder.append(date);
        builder.append("\", \"time\": \"");
        builder.append(time);
        builder.append("\", \"row\": ");
        builder.append(row);
        builder.append(", \"number\": ");
        builder.append(number);
        builder.append(", \"cost\": ");
        builder.append(cost);
        builder.append(", \"sold\": ");
        builder.append(sold ? "true" : "false");
        builder.append(" }\n");
        writer.write(builder.toString());
    }

    public static void main(String args[]) throws IOException {
        FileWriter writer = new FileWriter("/home/jdconrad/test/seats.json");
        int id = 0;

        for (int playCount = 0; playCount < 12; ++playCount) {
            String play = plays[playCount];
            String theatre;
            String[] actor;
            int startMonth;
            int endMonth;
            String time;

            if (playCount == 0) {
                theatre = theatres[0];
                actor = new String[] {actors[0], actors[1], actors[2], actors[3]};
                startMonth = 4;
                endMonth = 5;
                time = "3:00PM";
            } else if (playCount == 1) {
                theatre = theatres[0];
                actor = new String[] {actors[4], actors[5], actors[6], actors[7], actors[8], actors[9]};
                startMonth = 4;
                endMonth = 6;
                time = "8:00PM";
            } else if (playCount == 2) {
                theatre = theatres[0];
                actor = new String[] {actors[0], actors[1], actors[2], actors[3],
                                      actors[4], actors[5], actors[6], actors[7]};
                startMonth = 6;
                endMonth = 8;
                time = "3:00 PM";
            } else if (playCount == 3) {
                theatre = theatres[0];
                actor = new String[] {actors[9], actors[10], actors[11], actors[12], actors[13], actors[14],
                                      actors[15], actors[16], actors[17], actors[18], actors[19]};
                startMonth = 7;
                endMonth = 8;
                time = "8:00PM";
            } else if (playCount == 4) {
                theatre = theatres[0];
                actor = new String[] {actors[13], actors[14], actors[15], actors[17], actors[18], actors[19]};
                startMonth = 8;
                endMonth = 10;
                time = "3:00PM";
            } else if (playCount == 5) {
                theatre = theatres[0];
                actor = new String[] {actors[8], actors[9], actors[10], actors[11], actors[12]};
                startMonth = 8;
                endMonth = 10;
                time = "8:00PM";
            } else if (playCount == 6) {
                theatre = theatres[1];
                actor = new String[] {actors[10], actors[11], actors[12], actors[13], actors[14], actors[15], actors[16]};
                startMonth = 4;
                endMonth = 5;
                time = "11:00AM";
            } else if (playCount == 7) {
                theatre = theatres[1];
                actor = new String[] {actors[17], actors[18]};
                startMonth = 6;
                endMonth = 9;
                time = "2:00PM";
            } else if (playCount == 8) {
                theatre = theatres[1];
                actor = new String[] {actors[0], actors[1], actors[2], actors[3], actors[16]};
                startMonth = 10;
                endMonth = 11;
                time = "11:00AM";
            } else if (playCount == 9) {
                theatre = theatres[2];
                actor = new String[] {actors[1], actors[2], actors[3], actors[17], actors[18], actors[19]};
                startMonth = 3;
                endMonth = 6;
                time = "4:00PM";
            } else if (playCount == 10) {
                theatre = theatres[2];
                actor = new String[] {actors[2], actors[3], actors[4], actors[5]};
                startMonth = 7;
                endMonth = 8;
                time = "7:30PM";
            } else if (playCount == 11) {
                theatre = theatres[2];
                actor = new String[] {actors[7], actors[13], actors[14], actors[15], actors[16], actors[17]};
                startMonth = 9;
                endMonth = 12;
                time = "5:40PM";
            } else {
                throw new RuntimeException("too many plays");
            }

            int rows;
            int number;

            if (playCount < 6) {
               rows = 3;
               number = 12;
            } else if (playCount < 9) {
               rows = 5;
               number = 9;
            } else if (playCount < 12) {
               rows = 11;
               number = 15;
            } else {
                throw new RuntimeException("too many seats");
            }

            for (int month = startMonth; month <= endMonth; ++month) {
                for (int day = 1; day <= 14; ++day) {
                    for (int row = 1; row <= rows; ++row) {
                        for (int count = 1; count <= number; ++count) {
                            String date = "2018-" + month + "-" + day;
                            double cost = (25 - row) * 1.25;

                            writeSeat(writer, ++id, theatre, play, actor, date, time, row, count, cost, false);
                        }
                    }
                }
            }
        }

        writer.write("\n");
        writer.close();
        }
    }

    */

    // Initial Mappings:

    /*

    curl -X PUT "localhost:9200/seats" -H 'Content-Type: application/json' -d'
    {
      "mappings": {
        "seat": {
          "properties": {
            "theatre":  { "type": "keyword" },
            "play":     { "type": "text"    },
            "actors":   { "type": "text"    },
            "row":      { "type": "integer" },
            "number":   { "type": "integer" },
            "cost":     { "type": "double"  },
            "sold":     { "type": "boolean" },
            "datetime": { "type": "date"    },
            "date":     { "type": "keyword" },
            "time":     { "type": "keyword" }
          }
        }
      }
    }
    '

    */

// *** EXAMPLE: INGEST ***

    // Create Ingest to Modify Dates:

    /*

    curl -X PUT "localhost:9200/_ingest/pipeline/seats" -H 'Content-Type: application/json' -d'
    {
        "description": "update datetime for seats",
        "processors": [
          {
            "script": {
              "source": "String[] split(String s, char d) { int count = 0; for (char c : s.toCharArray()) { if (c == d) { ++count; } } if (count == 0) { return new String[] {s}; } String[] r = new String[count + 1]; int i0 = 0, i1 = 0; count = 0; for (char c : s.toCharArray()) { if (c == d) { r[count++] = s.substring(i0, i1); i0 = i1 + 1; } ++i1; } r[count] = s.substring(i0, i1); return r; } String[] dateSplit = split(ctx.date, (char)\"-\"); String year = dateSplit[0].trim(); String month = dateSplit[1].trim(); if (month.length() == 1) { month = \"0\" + month; } String day = dateSplit[2].trim(); if (day.length() == 1) { day = \"0\" + day; } boolean pm = ctx.time.substring(ctx.time.length() - 2).equals(\"PM\"); String[] timeSplit = split(ctx.time.substring(0, ctx.time.length() - 2), (char)\":\"); int hours = Integer.parseInt(timeSplit[0].trim()); int minutes = Integer.parseInt(timeSplit[1].trim()); if (pm) { hours += 12; } String dts = year + \"-\" + month + \"-\" + day + \"T\" + (hours < 10 ? \"0\" + hours : \"\" + hours) + \":\" + (minutes < 10 ? \"0\" + minutes : \"\" + minutes) + \":00+08:00\"; ZonedDateTime dt = ZonedDateTime.parse(dts, DateTimeFormatter.ISO_OFFSET_DATE_TIME); ctx.datetime = dt.getLong(ChronoField.INSTANT_SECONDS)*1000L;"
            }
          }
        ]
    }
    '

    */

    public void testIngestProcessorScript() {
        assertEquals(1535785200000L,
                exec("String[] split(String s, char d) {" +
                     "    int count = 0;" +
                     "    for (char c : s.toCharArray()) {" +
                     "        if (c == d) {" +
                     "            ++count;" +
                     "        }" +
                     "    }" +
                     "    if (count == 0) {" +
                     "        return new String[] {s};" +
                     "    }" +
                     "    String[] r = new String[count + 1];" +
                     "    int i0 = 0, i1 = 0;" +
                     "    count = 0;" +
                     "    for (char c : s.toCharArray()) {" +
                     "        if (c == d) {" +
                     "            r[count++] = s.substring(i0, i1);" +
                     "            i0 = i1 + 1;" +
                     "        }" +
                     "        ++i1;" +
                     "    }" +
                     "    r[count] = s.substring(i0, i1);" +
                     "    return r;" +
                     "}" +
                     "def x = ['date': '2018-9-1', 'time': '3:00 PM'];" +
                     "String[] dateSplit = split(x.date, (char)'-');" +
                     "String year = dateSplit[0].trim();" +
                     "String month = dateSplit[1].trim();" +
                     "if (month.length() == 1) {" +
                     "    month = '0' + month;" +
                     "}" +
                     "String day = dateSplit[2].trim();" +
                     "if (day.length() == 1) {" +
                     "    day = '0' + day;" +
                     "}" +
                     "boolean pm = x.time.substring(x.time.length() - 2).equals('PM');" +
                     "String[] timeSplit = split(x.time.substring(0, x.time.length() - 2), (char)':');" +
                     "int hours = Integer.parseInt(timeSplit[0].trim());" +
                     "String minutes = timeSplit[1].trim();" +
                     "if (pm) {" +
                     "    hours += 12;" +
                     "}" +
                     "String dts = year + '-' + month + '-' + day + " +
                     "'T' + (hours < 10 ? '0' + hours : '' + hours) + ':' + minutes + ':00+08:00';" +
                     "ZonedDateTime dt = ZonedDateTime.parse(dts, DateTimeFormatter.ISO_OFFSET_DATE_TIME);" +
                     "return dt.getLong(ChronoField.INSTANT_SECONDS) * 1000L"
                )
        );
    }

// *** UPLOAD ***

    // Post Generated Data:

    /*

    curl -XPOST localhost:9200/seats/seat/_bulk?pipeline=seats -H "Content-Type: application/x-ndjson" --data-binary "@/home/jdconrad/test/seats.json"

    */

// *** EXAMPLE: REINDEX ***

    // Create Mappings for Reindex:

    /*

    curl -X PUT "localhost:9200/afternoon" -H 'Content-Type: application/json' -d'
    {
      "mappings": {
        "seat": {
          "properties": {
            "theatre":       { "type": "keyword" },
            "play":          { "type": "text"    },
            "actors":        { "type": "text"    },
            "row":           { "type": "integer" },
            "number":        { "type": "integer" },
            "cost":          { "type": "double"  },
            "sold":          { "type": "boolean" },
            "sold_datetime": { "type": "date"    },
            "datetime":      { "type": "date"    }
          }
        }
      }
    }
    '

    */

    /*

    curl -X PUT "localhost:9200/evening" -H 'Content-Type: application/json' -d'
    {
      "mappings": {
        "seat": {
          "properties": {
            "theatre":       { "type": "keyword" },
            "play":          { "type": "text"    },
            "actors":        { "type": "text"    },
            "row":           { "type": "integer" },
            "number":        { "type": "integer" },
            "cost":          { "type": "double"  },
            "sold":          { "type": "boolean" },
            "sold_datetime": { "type": "date"    },
            "datetime":      { "type": "date"    }
          }
        }
      }
    }
    '

    */

    // Use the reindex script to create afternoon and evening indexes

    /*

    curl -X POST "localhost:9200/_reindex" -H 'Content-Type: application/json' -d'
    {
        "source": {
            "index": "seats"
        },
        "dest": {
            "index": "afternoon"
        },
        "script": {
            "source": "void setSold(Map _source) { if ((_source[\"number\"] + _source[\"row\"] % 4) % 3 == 0 || _source[\"number\"] % 5 == 0) { _source[\"sold\"] = true; _source[\"sold_datetime\"] = _source[\"datetime\"] - 86400000 * (1 + _source[\"number\"] % 14); } } void removeExtraneous(Map _source) { _source.remove(\"date\"); _source.remove(\"time\"); } setSold(ctx[\"_source\"]); removeExtraneous(ctx[\"_source\"]); Instant instant = Instant.ofEpochMilli(ctx[\"_source\"][\"datetime\"]); ZonedDateTime dt = instant.atZone(ZoneId.of(\"GMT+8\")); if (dt.getHour() > 16) { ctx[\"_index\"] = \"evening\"; }"
        }
    }
    '

    */

    public void testReindexScript() {
        Map<String, Object> _source = new HashMap<>();
        _source.put("number", 10);
        _source.put("row", 1);
        _source.put("sold", true);
        _source.put("datetime", 1522573200000L);
        _source.put("sold_datetime", 1521622800000L);
        Map<String, Object> ctx = new HashMap<>();
        ctx.put("_source", _source);
        ctx.put("_index", "evening");

        assertEquals(ctx, exec(
                "void setSold(Map _source) {" +
                "    if ((_source[\"number\"] + _source[\"row\"] % 4) % 3 == 0 || _source[\"number\"] % 5 == 0) {" +
                "        _source[\"sold\"] = true;" +
                "        _source[\"sold_datetime\"] = _source[\"datetime\"] - 86400000 * (1 + _source[\"number\"] % 14);" +
                "    }" +
                "}" +
                "" +
                "void removeExtraneous(Map _source) {" +
                "    _source.remove(\"date\");" +
                "    _source.remove(\"time\");" +
                "}" +
                "" +
                "Map _source = new HashMap();" +
                "_source[\"number\"] = 10;" +
                "_source[\"row\"] = 1;" +
                "_source[\"sold\"] = false;" +
                "_source[\"datetime\"] = 1522573200000L;" +
                "_source[\"date\"] = \"2018-08-17\";" +
                "_source[\"time\"] = \"12:30PM\";" +
                "Map rtn = new HashMap();" +
                "rtn[\"_source\"] = _source;" +
                "" +
                "setSold(rtn[\"_source\"]);" +
                "removeExtraneous(rtn[\"_source\"]);" +
                "" +
                "Instant instant = Instant.ofEpochMilli(rtn[\"_source\"][\"datetime\"]);" +
                "ZonedDateTime dt = instant.atZone(ZoneId.of(\"GMT+8\"));" +
                "" +
                "if (dt.getHour() > 16) {" +
                "    rtn[\"_index\"] = \"evening\";" +
                "}" +
                "" +
                "return rtn;"
            )
        );
    }
}
