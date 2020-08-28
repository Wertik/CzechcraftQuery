package space.devport.wertik.czechcraftquery;

import lombok.experimental.UtilityClass;

import java.util.StringTokenizer;

@UtilityClass
public class VersionUtil {

    public int[] canonicalVersion(String version) {

        int[] canonicalVersion = new int[]{0, 0, 0, 0};
        StringTokenizer tokenizer = new StringTokenizer(version, ".");

        String token = tokenizer.nextToken();
        canonicalVersion[0] = Integer.parseInt(token);
        token = tokenizer.nextToken();
        StringTokenizer subTokenizer;

        if (!token.contains("_")) {
            canonicalVersion[1] = Integer.parseInt(token);
        } else {
            subTokenizer = new StringTokenizer(token, "_");

            canonicalVersion[1] = Integer.parseInt(subTokenizer.nextToken());
            canonicalVersion[3] = Integer.parseInt(subTokenizer.nextToken());
        }

        if (tokenizer.hasMoreTokens()) {
            token = tokenizer.nextToken();

            if (!token.contains("_")) {
                canonicalVersion[2] = Integer.parseInt(token);
                if (tokenizer.hasMoreTokens()) {
                    canonicalVersion[3] = Integer.parseInt(tokenizer.nextToken());
                }
            } else {
                subTokenizer = new StringTokenizer(token, "_");

                canonicalVersion[2] = Integer.parseInt(subTokenizer.nextToken());
                canonicalVersion[3] = Integer.parseInt(subTokenizer.nextToken());
            }
        }
        return canonicalVersion;
    }

    public int compareVersions(String version1, String version2) {

        int[] canonical1 = canonicalVersion(version1);
        int[] canonical2 = canonicalVersion(version2);

        for (int n = 0; n < canonical1.length && n < canonical2.length; n++) {
            if (canonical1[n] < canonical2[n])
                return -1;
            else if (canonical1[n] > canonical2[n])
                return 1;
        }

        return 0;
    }
}