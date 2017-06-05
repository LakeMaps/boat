messages = [
    'BoatConfig',
    'Bytes',
    'ControlMode',
    'Gps',
    'MissionInformation',
    'Motion',
    'PidControllerGains',
    'Position',
    'TypedMessage',
    'Velocity',
    'Waypoint',
];
module.exports = Object.assign({}, null);
messages.forEach((name) =>
    module.exports[name] = require(`./schemas/${name}_pb`)[name]);
