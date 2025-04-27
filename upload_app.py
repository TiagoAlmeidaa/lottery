import argparse
import sys

from apiclient import sample_tools
from oauth2client import client

TRACK = 'alpha'

argparse = argparse.ArgumentParser(add_help=False)
argparse.add_argument('package_name', help='The app package name. Example: com.github.example')
argparse.add_argument('apk_file', nargs='?', help='The path to the APK file to upload.')

def main(argv):
    print(argv)
    print("-> preparing to publish on: %s" % TRACK)

    # Getting the Android Publisher API service
    service, flags = sample_tools.init(
        argv,
        'androidpublisher',
        'v3',
        __doc__,
        __file__,
        parents=[argparse],
        scope='https://www.googleapis.com/auth/androidpublisher'
    )

    print("-> androidpublisher API initialized")

    package_name = flags.package_name
    apk_file = flags.apk_file

    print("-> package: %s" % package_name)
    print("-> apk: %s" % apk_file)

    try:
        # Create a new edit for the app
        edit_request = service.edits().insert(body={}, packageName=package_name)
        result = edit_request.execute()
        edit_id = result['id']

        print("-> app edit created")

        # Upload the APK to the current edit
        apk_response = service.edits().apks().upload(
            editId=edit_id,
            packageName=package_name,
            media_body=apk_file
        ).execute()

        print("-> apk with version code %d uploaded" % apk_response['versionCode'])

        # Updating release track information
        track_response = service.edits().tracks().update(
            editId=edit_id,
            track=TRACK,
            packageName=package_name,
            body={'releases': [{
                'versionCodes': [str(apk_response['versionCode'])],
                'status': 'inProgress',
                'userFraction': 0.1
            }]}).execute()

        print("-> track: %s" % track_response['track'])
        print("-> release: %s" % track_response['release'])

        # Committing changes
        commit_request = service.edits.commit(
            editId=edit_id,
            packageName=package_name
        ).execute()

        print("-> changes has been commit (%s)" % commit_request['id'])

    except client.AccessTokenRefreshError:
        print("-> The credentials have been revoked or expired.")


if __name__ == '__main__':
    main(sys.argv)
