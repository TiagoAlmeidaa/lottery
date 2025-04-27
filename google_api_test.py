import argparse
import sys

from apiclient import sample_tools
from oauth2client import client

# Declare command-line flags.
argparser = argparse.ArgumentParser(add_help=False)
argparser.add_argument('package_name',
                       help='The package name. Example: com.android.sample')

def main(argv):
    print()
    print("google_api_test.py Starting")

    # Getting the androidpublisher v3 service
    service, flags = sample_tools.init(
        argv,
        'androidpublisher',
        'v3',
        __doc__,
        __file__,
        parents=[argparser],
        scope='https://www.googleapis.com/auth/androidpublisher')

    package_name = flags.package_name

    try:
        # Getting the edit id
        edit_request = service.edits().insert(body={}, packageName=package_name)
        result = edit_request.execute()
        edit_id = result['id']

        # Requesting available tracks
        tracks_result = service.edits().tracks().list(
            packageName=package_name,
            editId=edit_id
        ).execute()

        # Here I'll just print information about the available tracks
        tracks = tracks_result['tracks']
        for track in tracks:
            print()
            print("[INFORMATION] Track: %s" % track['track'])
            releases = track['releases']
            for release in releases:
                print("[INFORMATION] Release: %s" % release)

    except client.AccessTokenRefreshError:
        print('The credentials have been revoked or expired, please re-run the '
              'application to re-authorize')
    finally:
        print()
        print("google_api_test.py Ending")
        print()


if __name__ == '__main__':
    main(sys.argv)
