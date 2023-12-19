import dayjs from 'dayjs/esm';

import { IEvent, NewEvent } from './event.model';

export const sampleWithRequiredData: IEvent = {
  id: 240,
  label: 'Unvakm4',
  dateStart: dayjs('2023-12-18'),
  dateEnd: dayjs('2023-12-18'),
  place: 'tandis que menacer collègue',
};

export const sampleWithPartialData: IEvent = {
  id: 21555,
  label: 'Etq6',
  description: 'dans la mesure où',
  dateStart: dayjs('2023-12-17'),
  dateEnd: dayjs('2023-12-17'),
  place: 'blablabla au-devant',
  placeDetails: 'plus conseil d’administration',
  note: 'soit simple peut-être',
};

export const sampleWithFullData: IEvent = {
  id: 30796,
  label: 'Kxdtnn5',
  description: 'ahX',
  theme: 'venir aussitôt que',
  dateStart: dayjs('2023-12-18'),
  dateEnd: dayjs('2023-12-18'),
  place: 'secours collègue résigner',
  placeDetails: 'entre',
  adress: 'dessus',
  note: 'commis terriblement cultiver',
};

export const sampleWithNewData: NewEvent = {
  label: 'Ba8',
  dateStart: dayjs('2023-12-18'),
  dateEnd: dayjs('2023-12-18'),
  place: 'considérable dans la mesure où',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
