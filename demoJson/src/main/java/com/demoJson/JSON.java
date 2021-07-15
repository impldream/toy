package com.demoJson;

public abstract class JSON {

    public static void consumeWs(StringBuilder text) {
        while (text.charAt(0) == ' ') {
            text.delete(0, 1);
        }
    }

    public static Object parse(StringBuilder text) {
        char value;
        consumeWs(text);
        value = text.charAt(0);

        switch (value) {
            case '[':
                return parseArray(text);
            case '{':
                return parseObject(text);
            case '\"':
                return parseString(text);
            case 't':
            case 'f':
                return parseBoolean(text);
            case 'n':
                return parseNull(text);
            default:
                if ((value <= '9' && value >= '0') || value == '-') {
                    return parseNumber(text);
                }

        }
        return new JSONObject();
    }

    public static Boolean parseBoolean(StringBuilder text) {
        if (text.toString().startsWith("true")) {
            text.delete(0, 4);
            return true;
        } else if (text.toString().startsWith("false")) {
            text.delete(0, 5);
            return false;
        }

        return null;
    }

    public static String parseString(StringBuilder text) {
        StringBuilder sb = new StringBuilder();
        int offset = 1;
        while (text.charAt(offset) != '\"') {
            if (text.charAt(offset) == '\\') {
                ++offset;
                switch (text.charAt(offset)) {
                    case '\"':
                        sb.append('\"');
                        break;
                    case '\\':
                        sb.append('\\');
                        break;
                    case '/':
                        sb.append('\b');
                        break;
                    case 'b':
                        sb.append('\b');
                        break;
                    case 'f':
                        sb.append('\f');
                        break;
                    case 'n':
                        sb.append('\n');
                        break;
                    case 'r':
                        sb.append('\r');
                        break;
                    case 't':
                        sb.append('\t');
                        break;
                    case 'u': {
                        sb.append("\\u");
                        char c;
                        for (int i = 1; i < 5; i++) {
                            c = text.charAt(offset + i);
                            if ((c >= '0' && c <= '9') || (c >= 'a' && c <= 'f') || c >= 'A' && c <= 'F') {
                                sb.append(c);
                            } else {
                                throw new JSONException("ERROR! String: Expected hex character in unicode!");
                            }
                        }
                        offset += 4;
                    }
                    break;
                    default:
                        sb.append('\\');
                        break;
                }
            } else {
                sb.append(text.charAt(offset));
            }
            ++offset;
        }
        ++offset;
        text.delete(0, offset);
        return sb.toString();
    }

    public static Object parseNull(StringBuilder text) {
        if (!text.toString().startsWith("null")) {
            throw new JSONException("ERROR: Expected null!");
        }
        text.delete(0, 4);
        return "null";
    }

    public static Object parseNumber(StringBuilder text) {
        StringBuilder val = new StringBuilder();
        StringBuilder exp_str = new StringBuilder();
        char c = text.charAt(0);
        boolean isDouble = false;
        long exp = 0;

        while (text.length() > 0) {
            c = text.charAt(0);

            if (c == '-' || (c >= '0' && c <= '9')) {
                val.append(c);
            } else if (c == '.') {
                val.append(c);
                isDouble = true;
            } else {
                break;
            }
            text.delete(0, 1);
        }

        if (c == 'E' || c == 'e') {
            c = text.charAt(0);
            text.delete(0, 1);

            while (text.length() > 0) {
                c = text.charAt(0);

                if (c >= '0' && c <= '9') {
                    exp_str.append(c);
                } else if (c != ' ' && c != ',' && c != ']' && c != '}') {
                    throw new JSONException("ERROR! Number: Expected a number for exponent!");
                } else {
                    break;
                }
                text.delete(0, 1);
            }
            exp += Long.valueOf(exp_str.toString());
        } else if (c != ' ' && c != ',' && c != ']' && c != '}') {
            throw new JSONException("ERROR! Number: unexpected character " + c);
        }

        if (isDouble) {
            Double number = Double.valueOf(val.toString()) * Math.pow(10, exp);
            return number;
        } else {
            long number = 0l;
            if (!exp_str.toString().isEmpty()) {
                number = Long.valueOf(val.toString()) * (long) Math.pow(10, exp);
            } else {
                number = Long.parseLong(val.toString());
            }

            return number;
        }
    }

    public static JSONObject parseObject(StringBuilder text) {
        text.delete(0, 1);

        consumeWs(text);
        if (text.charAt(0) == '}') {
            text.delete(0, 1);
            return new JSONObject();
        }
        JSONObject object = new JSONObject();

        while (text.length() > 0) {
            String key = (String)parse(text);
            consumeWs(text);

            if (text.charAt(0) != ':') {
                throw new JSONException("Error! Object: Expected colon!");
            }
            text.delete(0, 1);
            consumeWs(text);

            Object value = parse(text);
            object.put(key, value);

            consumeWs(text);
            if (text.charAt(0) == ',') {
                text.delete(0, 1);
                continue;
            }
            else if (text.charAt(0) == '}') {
                text.delete(0, 1);
                break;
            }
            else {
                throw new JSONException("ERROR! Object: Expected comma, found " + text.charAt(0));
            }
        }

        return object;
    }

    public static JSONArray parseArray(StringBuilder text) {
        text.delete(0, 1);
        consumeWs(text);
        if (text.charAt(0) == ']') {
            text.delete(0, 1);
            return new JSONArray();
        }

        JSONArray jsonArray = new JSONArray();
        while (text.length() > 0)  {
            Object value = parse(text);
            jsonArray.add(value);

            consumeWs(text);

            if (text.charAt(0) == ',') {
                text.delete(0, 1);
                continue;
            }
            else if (text.charAt(0) == ']') {
                text.delete(0, 1);
                break;
            }
            else {
                throw new JSONException("ERROR! Array: Expected ',' or  ']', found " + text.charAt(0));
            }
        }

        return jsonArray;
    }

}
