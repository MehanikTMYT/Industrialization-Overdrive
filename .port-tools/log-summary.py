from pathlib import Path
import re,sys
s=Path(sys.argv[1]).read_text();s=s.split('> Task :compileJava',1)[-1].split('FAILURE:',1)[0]
for m in re.finditer(r'^([^\n]+\.java):(\d+): error: ([^\n]+)\n((?:(?![^\n]+\.java:\d+: error:)[\s\S])*?)(?=^/root/|\Z)',s,re.M):
 print(m[1].split('/src/main/java/')[-1]+':'+m[2]+' '+m[3]+' '+m[4].strip().replace('\n',' ')[:240])
