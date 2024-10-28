import os

new_line = "\n"
equals_sign = "="
file_path = "version.properties"

def generate_file_content(tag):
    file = open(file_path, "r")
    content = []

    for index, line in enumerate(file.readlines()):
        if index == 0:
            line_split = line.strip().split("=")
            content.append("{}{}{}{}".format(line_split[0], equals_sign, tag, new_line))
        elif index == 1:
            line_split = line.strip().split("=")
            new_version_code = int(line_split[1]) + 1
            content.append("{}{}{}".format(line_split[0], equals_sign, new_version_code))

    file.close()
    return content

def override_file_content(content):
    file = open(file_path, "w")
    file.writelines(content)
    file.close()

def increment_version(tag):
    print("Version increment - New Tag: " + tag + "\n")
    override_file_content(generate_file_content(tag))
    print("Version increment - Finished!\n")

increment_version(os.getenv('VERSION_TAG'))
